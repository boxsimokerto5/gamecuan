package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.DailyMissionEntity
import com.example.data.db.PointHistoryEntity
import com.example.data.db.UserWalletEntity
import com.example.data.db.WithdrawalTransactionEntity
import com.example.data.model.GamePixItem
import com.example.data.repository.GameRepository
import com.example.data.repository.UserRewardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameCuanViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val rewardRepo = UserRewardRepository(db)
    private val gameRepo = GameRepository()

    val walletState: StateFlow<UserWalletEntity> = rewardRepo.walletFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserWalletEntity(points = 3500)
        ).let { flow ->
            // Ensure null fallback
            flow.combine(MutableStateFlow(UserWalletEntity(points = 3500))) { current, fallback ->
                current ?: fallback
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserWalletEntity(points = 3500))
        }

    val missionsState: StateFlow<List<DailyMissionEntity>> = rewardRepo.missionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val transactionsState: StateFlow<List<WithdrawalTransactionEntity>> = rewardRepo.transactionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val historyState: StateFlow<List<PointHistoryEntity>> = rewardRepo.historyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _rawGames = MutableStateFlow<List<GamePixItem>>(emptyList())
    private val _isLoadingGames = MutableStateFlow(true)
    val isLoadingGames: StateFlow<Boolean> = _isLoadingGames.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Semua")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Filtered games based on search and category
    val filteredGames: StateFlow<List<GamePixItem>> = combine(
        _rawGames,
        _searchQuery,
        _selectedCategory
    ) { games, query, category ->
        games.filter { game ->
            val matchQuery = query.isBlank() ||
                    game.title.contains(query, ignoreCase = true) ||
                    (game.category?.contains(query, ignoreCase = true) == true) ||
                    (game.description?.contains(query, ignoreCase = true) == true)

            val matchCategory = category == "Semua" ||
                    game.category.equals(category, ignoreCase = true)

            matchQuery && matchCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeGameForPlaying = MutableStateFlow<GamePixItem?>(null)
    val activeGameForPlaying: StateFlow<GamePixItem?> = _activeGameForPlaying.asStateFlow()

    private val _notificationMessage = MutableStateFlow<String?>(null)
    val notificationMessage: StateFlow<String?> = _notificationMessage.asStateFlow()

    private val prefs = application.getSharedPreferences("game_cuan_prefs", android.content.Context.MODE_PRIVATE)
    private val _isDarkTheme = MutableStateFlow(prefs.getBoolean("is_dark_theme", true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    val supabaseManager = com.example.data.supabase.SupabaseManager.getInstance(application)
    val supabaseUserId = supabaseManager.currentUserId
    val supabaseUserEmail = supabaseManager.currentUserEmail
    val isEmailConfirmed = supabaseManager.isEmailConfirmed
    val supabaseUrl = supabaseManager.projectUrl
    val supabaseAnonKey = supabaseManager.anonKey

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    init {
        viewModelScope.launch {
            rewardRepo.initializeUserDataIfNeeded()
            loadGames()
            if (supabaseManager.isConfigured() && supabaseManager.currentUserId.value != null) {
                syncWithSupabase()
            }
        }
    }

    fun signUpSupabase(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = supabaseManager.signUp(email, pass)
            _isAuthLoading.value = false
            result.onSuccess {
                _notificationMessage.value = "Akun berhasil dibuat! Silakan periksa inbox email Anda untuk klik link verifikasi."
                onSuccess()
                syncWithSupabase()
            }.onFailure { err ->
                _notificationMessage.value = "Pendaftaran gagal: ${err.message}"
            }
        }
    }

    fun signInSupabase(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = supabaseManager.signIn(email, pass)
            _isAuthLoading.value = false
            result.onSuccess {
                _notificationMessage.value = "Berhasil masuk ke akun Game Cuan!"
                onSuccess()
                syncWithSupabase()
            }.onFailure { err ->
                _notificationMessage.value = "Gagal masuk: ${err.message}"
            }
        }
    }

    fun signOutSupabase() {
        supabaseManager.clearSession()
        _notificationMessage.value = "Berhasil keluar dari akun Supabase."
    }

    fun saveSupabaseConfig(url: String, key: String) {
        supabaseManager.saveConfiguration(url, key)
        _notificationMessage.value = "Konfigurasi Supabase berhasil diperbarui!"
        viewModelScope.launch {
            if (supabaseManager.currentUserId.value != null) {
                syncWithSupabase()
            }
        }
    }

    fun syncWithSupabase() {
        viewModelScope.launch {
            val userId = supabaseManager.currentUserId.value ?: return@launch
            val email = supabaseManager.currentUserEmail.value ?: ""

            // 1. Fetch cloud profile
            val cloudProfileResult = supabaseManager.fetchProfile()
            val currentWallet = walletState.value

            cloudProfileResult.onSuccess { cloudProfile ->
                if (cloudProfile != null) {
                    // Update local if cloud has higher or different stats
                    if (cloudProfile.points > currentWallet.points) {
                        rewardRepo.withdrawPoints("", "", 0, currentWallet.points - cloudProfile.points)
                    }
                }
            }

            // 2. Upload latest local wallet to Supabase
            val profileToUpload = com.example.data.supabase.SupabaseProfile(
                id = userId,
                email = email,
                points = currentWallet.points,
                streakDays = currentWallet.streakDays,
                lastCheckInDate = currentWallet.lastCheckInDate,
                gamesPlayed = currentWallet.gamesPlayed,
                minutesPlayed = currentWallet.minutesPlayed
            )
            val uploadResult = supabaseManager.syncProfile(profileToUpload)
            uploadResult.onSuccess {
                _notificationMessage.value = "Poin dan akun Anda tersinkronisasi dengan Supabase Cloud! ☁️"
            }.onFailure { err ->
                // Don't show disruptive error if tables not yet created on user's Supabase instance
                _notificationMessage.value = "Status sinkronisasi: ${err.message}"
            }
        }
    }

    fun toggleDarkTheme() {
        val newTheme = !_isDarkTheme.value
        _isDarkTheme.value = newTheme
        prefs.edit().putBoolean("is_dark_theme", newTheme).apply()
    }

    fun loadGames() {
        viewModelScope.launch {
            _isLoadingGames.value = true
            val result = gameRepo.getGames()
            result.onSuccess { list ->
                _rawGames.value = list
            }.onFailure {
                _rawGames.value = GameRepository.fallbackGames
            }
            _isLoadingGames.value = false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun checkInToday() {
        viewModelScope.launch {
            val result = rewardRepo.performDailyCheckIn()
            result.onSuccess { pts ->
                _notificationMessage.value = "Selamat! Check-in berhasil, kamu dapat +$pts Poin! 🎉"
            }.onFailure { err ->
                _notificationMessage.value = err.message ?: "Gagal melakukan check-in"
            }
        }
    }

    fun claimMission(missionId: String) {
        viewModelScope.launch {
            val result = rewardRepo.claimMissionReward(missionId)
            result.onSuccess { pts ->
                _notificationMessage.value = "Reward misi diklaim! +$pts Poin ditambahkan ke dompet 🪙"
            }.onFailure { err ->
                _notificationMessage.value = err.message ?: "Gagal mengklaim misi"
            }
        }
    }

    fun openGame(game: GamePixItem) {
        _activeGameForPlaying.value = game
        viewModelScope.launch {
            rewardRepo.recordCatalogExploration()
        }
    }

    fun closeGame() {
        _activeGameForPlaying.value = null
    }

    fun recordGameFinished(durationSeconds: Int) {
        viewModelScope.launch {
            val minutes = (durationSeconds / 60).coerceAtLeast(1)
            rewardRepo.recordGamePlay(minutes)
            _notificationMessage.value = "Keren! Bermain $durationSeconds detik. Poin & progres misi tercatat! 🎮"
        }
    }

    fun withdrawPoints(provider: String, phone: String, amountRupiah: Int, pointsRequired: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = rewardRepo.withdrawPoints(provider, phone, amountRupiah, pointsRequired)
            result.onSuccess { tx ->
                _notificationMessage.value = "Penukaran Rp ${"%,d".format(amountRupiah)} ke $provider berhasil dikonfirmasi! Kode: ${tx.referenceCode}"
                onSuccess()

                // If user logged in with Supabase, record withdrawal and sync wallet
                val userId = supabaseManager.currentUserId.value
                if (userId != null) {
                    supabaseManager.recordWithdrawal(
                        com.example.data.supabase.SupabaseWithdrawal(
                            userId = userId,
                            walletProvider = provider,
                            phoneNumber = phone,
                            amountRupiah = amountRupiah,
                            pointsDeducted = pointsRequired,
                            referenceCode = tx.referenceCode
                        )
                    )
                    syncWithSupabase()
                }
            }.onFailure { err ->
                _notificationMessage.value = err.message ?: "Penarikan gagal diproses"
            }
        }
    }

    fun shareAppAction() {
        viewModelScope.launch {
            rewardRepo.recordShareAction()
            _notificationMessage.value = "Tautan Game Cuan disalin! Misi berbagi selesai! ✨"
        }
    }

    fun clearNotification() {
        _notificationMessage.value = null
    }
}

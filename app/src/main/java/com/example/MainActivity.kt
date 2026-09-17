package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.GameCuanHeader
import com.example.ui.screens.GamePlayerScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.MissionsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameCuanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: GameCuanViewModel = viewModel()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            MyApplicationTheme(darkTheme = isDarkTheme) {
                GameCuanApp(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme
                )
            }
        }
    }
}

enum class NavigationTab(val title: String, val testTag: String) {
    MISI("Misi", "nav_tab_misi"),
    GAMES("Katalog Game", "nav_tab_game"),
    WALLET("Dompet", "nav_tab_dompet")
}

@Composable
fun GameCuanApp(
    viewModel: GameCuanViewModel = viewModel(),
    isDarkTheme: Boolean = true
) {
    val context = LocalContext.current
    var currentTab by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    val wallet by viewModel.walletState.collectAsStateWithLifecycle()
    val missions by viewModel.missionsState.collectAsStateWithLifecycle()
    val filteredGames by viewModel.filteredGames.collectAsStateWithLifecycle()
    val isLoadingGames by viewModel.isLoadingGames.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val transactions by viewModel.transactionsState.collectAsStateWithLifecycle()
    val pointHistory by viewModel.historyState.collectAsStateWithLifecycle()
    val activeGame by viewModel.activeGameForPlaying.collectAsStateWithLifecycle()
    val notificationMessage by viewModel.notificationMessage.collectAsStateWithLifecycle()

    val supabaseUserEmail by viewModel.supabaseUserEmail.collectAsStateWithLifecycle()
    val isEmailConfirmed by viewModel.isEmailConfirmed.collectAsStateWithLifecycle()
    val supabaseUrl by viewModel.supabaseUrl.collectAsStateWithLifecycle()
    val supabaseAnonKey by viewModel.supabaseAnonKey.collectAsStateWithLifecycle()
    val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()

    var showAuthDialog by remember { mutableStateOf(false) }
    var showConfigDialog by remember { mutableStateOf(false) }

    // Handle toast notification
    LaunchedEffect(notificationMessage) {
        notificationMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                GameCuanHeader(
                    points = wallet.points,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = { viewModel.toggleDarkTheme() },
                    onWalletClicked = { currentTab = 2 }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationTab.values().forEachIndexed { index, tab ->
                        val isSelected = currentTab == index
                        val (iconVector, outlinedVector) = when (tab) {
                            NavigationTab.MISI -> Icons.Filled.EmojiEvents to Icons.Outlined.EmojiEvents
                            NavigationTab.GAMES -> Icons.Filled.SportsEsports to Icons.Outlined.SportsEsports
                            NavigationTab.WALLET -> Icons.Filled.AccountBalanceWallet to Icons.Outlined.AccountBalanceWallet
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) iconVector else outlinedVector,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BrandPrimary,
                                selectedTextColor = BrandPrimary,
                                indicatorColor = BrandPrimary.copy(alpha = 0.12f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.testTag(tab.testTag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                label = "tab_transition"
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> MissionsScreen(
                        wallet = wallet,
                        missions = missions,
                        onCheckInClicked = { viewModel.checkInToday() },
                        onClaimMission = { missionId -> viewModel.claimMission(missionId) },
                        onNavigateToGames = { currentTab = 1 },
                        onShareApp = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Ayo mainkan game seru di Game Cuan dan kumpulkan poin untuk ditukar jadi saldo DANA, GoPay, dan OVO! Download sekarang: https://gamepix.com"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Game Cuan")
                            context.startActivity(shareIntent)
                            viewModel.shareAppAction()
                        }
                    )

                    1 -> GamesScreen(
                        games = filteredGames,
                        isLoading = isLoadingGames,
                        searchQuery = searchQuery,
                        onSearchChanged = { viewModel.setSearchQuery(it) },
                        selectedCategory = selectedCategory,
                        onCategorySelected = { viewModel.setSelectedCategory(it) },
                        onGameClicked = { game -> viewModel.openGame(game) },
                        onRefresh = { viewModel.loadGames() }
                    )

                    2 -> WalletScreen(
                        wallet = wallet,
                        transactions = transactions,
                        pointHistory = pointHistory,
                        onWithdraw = { provider, phone, amountRupiah, pointsRequired, onSuccess ->
                            viewModel.withdrawPoints(provider, phone, amountRupiah, pointsRequired, onSuccess)
                        },
                        userEmail = supabaseUserEmail,
                        isEmailConfirmed = isEmailConfirmed,
                        isSupabaseConfigured = viewModel.supabaseManager.isConfigured(),
                        onOpenAuthDialog = { showAuthDialog = true },
                        onOpenConfigDialog = { showConfigDialog = true },
                        onSyncNow = { viewModel.syncWithSupabase() },
                        onSignOut = { viewModel.signOutSupabase() }
                    )
                }
            }
        }

        // Supabase Auth Dialog (Login & Register manual + Email verification notice)
        if (showAuthDialog) {
            com.example.ui.components.SupabaseAuthDialog(
                isLoading = isAuthLoading,
                onDismiss = { showAuthDialog = false },
                onSignIn = { email, pass ->
                    viewModel.signInSupabase(email, pass, onSuccess = { showAuthDialog = false })
                },
                onSignUp = { email, pass ->
                    viewModel.signUpSupabase(email, pass, onSuccess = { showAuthDialog = false })
                }
            )
        }

        // Supabase Config Dialog (Project URL & Anon Key)
        if (showConfigDialog) {
            com.example.ui.components.SupabaseConfigDialog(
                currentUrl = supabaseUrl,
                currentKey = supabaseAnonKey,
                onDismiss = { showConfigDialog = false },
                onSave = { url, key ->
                    viewModel.saveSupabaseConfig(url, key)
                }
            )
        }

        // In-App Game Player Full Screen Overlay
        activeGame?.let { game ->
            GamePlayerScreen(
                game = game,
                onClose = { durationSec ->
                    viewModel.closeGame()
                    viewModel.recordGameFinished(durationSec)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

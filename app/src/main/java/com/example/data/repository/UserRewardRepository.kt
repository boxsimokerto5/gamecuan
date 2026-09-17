package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.db.DailyMissionEntity
import com.example.data.db.PointHistoryEntity
import com.example.data.db.UserWalletEntity
import com.example.data.db.WithdrawalTransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class UserRewardRepository(private val database: AppDatabase) {

    private val walletDao = database.userWalletDao()
    private val missionDao = database.dailyMissionDao()
    private val transactionDao = database.withdrawalTransactionDao()
    private val historyDao = database.pointHistoryDao()

    val walletFlow: Flow<UserWalletEntity?> = walletDao.getUserWallet()
    val missionsFlow: Flow<List<DailyMissionEntity>> = missionDao.getAllMissions()
    val transactionsFlow: Flow<List<WithdrawalTransactionEntity>> = transactionDao.getAllTransactions()
    val historyFlow: Flow<List<PointHistoryEntity>> = historyDao.getRecentHistory()

    suspend fun initializeUserDataIfNeeded() = withContext(Dispatchers.IO) {
        val existingWallet = walletDao.getUserWalletDirect()
        if (existingWallet == null) {
            walletDao.insertOrUpdate(
                UserWalletEntity(
                    id = 1,
                    points = 3500, // Starter bonus
                    lastCheckInDate = "",
                    streakDays = 1,
                    gamesPlayed = 0,
                    minutesPlayed = 0
                )
            )
            historyDao.insertHistory(
                PointHistoryEntity(
                    title = "Bonus Pengguna Baru",
                    pointsChange = 3500,
                    isAddition = true
                )
            )
        }

        val initialMissions = listOf(
            DailyMissionEntity(
                id = "daily_checkin",
                title = "Check-in Harian",
                description = "Buka aplikasi dan klaim reward check-in harianmu",
                rewardPoints = 200,
                targetCount = 1,
                currentCount = 0,
                iconName = "checkin"
            ),
            DailyMissionEntity(
                id = "play_1_game",
                title = "Mainkan 1 Game",
                description = "Buka dan mainkan game apa saja selama minimal 1 menit",
                rewardPoints = 300,
                targetCount = 1,
                currentCount = 0,
                iconName = "gamepad"
            ),
            DailyMissionEntity(
                id = "play_3_minutes",
                title = "Gamer Sejati (3 Menit)",
                description = "Habiskan waktu bermain game selama minimal 3 menit",
                rewardPoints = 500,
                targetCount = 3,
                currentCount = 0,
                iconName = "timer"
            ),
            DailyMissionEntity(
                id = "explore_catalog",
                title = "Jelajahi Katalog Game",
                description = "Buka detail atau preview 3 game di katalog Game Cuan",
                rewardPoints = 150,
                targetCount = 3,
                currentCount = 0,
                iconName = "explore"
            ),
            DailyMissionEntity(
                id = "share_app",
                title = "Bagikan Game Cuan",
                description = "Bagikan keseruan Game Cuan ke teman atau medsos",
                rewardPoints = 250,
                targetCount = 1,
                currentCount = 0,
                iconName = "share"
            )
        )
        missionDao.insertInitialMissions(initialMissions)
    }

    suspend fun performDailyCheckIn(): Result<Int> = withContext(Dispatchers.IO) {
        val currentWallet = walletDao.getUserWalletDirect() ?: UserWalletEntity()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (currentWallet.lastCheckInDate == today) {
            return@withContext Result.failure(Exception("Kamu sudah melakukan check-in hari ini!"))
        }

        val nextStreak = if (currentWallet.streakDays >= 7) 1 else currentWallet.streakDays + 1
        val rewardPoints = when (nextStreak) {
            1 -> 150
            2 -> 250
            3 -> 350
            4 -> 500
            5 -> 750
            6 -> 1000
            else -> 2000
        }

        walletDao.insertOrUpdate(
            currentWallet.copy(
                points = currentWallet.points + rewardPoints,
                lastCheckInDate = today,
                streakDays = nextStreak
            )
        )

        historyDao.insertHistory(
            PointHistoryEntity(
                title = "Check-in Hari ke-$nextStreak",
                pointsChange = rewardPoints,
                isAddition = true
            )
        )

        // Also update mission daily_checkin progress
        missionDao.incrementProgress("daily_checkin", 1)

        Result.success(rewardPoints)
    }

    suspend fun recordGamePlay(minutes: Int) = withContext(Dispatchers.IO) {
        walletDao.incrementGamesPlayed()
        if (minutes > 0) {
            walletDao.addMinutesPlayed(minutes)
            missionDao.incrementProgress("play_3_minutes", minutes)
        }
        missionDao.incrementProgress("play_1_game", 1)
    }

    suspend fun recordCatalogExploration() = withContext(Dispatchers.IO) {
        missionDao.incrementProgress("explore_catalog", 1)
    }

    suspend fun recordShareAction() = withContext(Dispatchers.IO) {
        missionDao.incrementProgress("share_app", 1)
    }

    suspend fun claimMissionReward(missionId: String): Result<Int> = withContext(Dispatchers.IO) {
        val mission = missionDao.getMissionById(missionId)
            ?: return@withContext Result.failure(Exception("Misi tidak ditemukan"))

        if (!mission.isCompleted) {
            return@withContext Result.failure(Exception("Misi belum selesai"))
        }

        if (mission.isClaimed) {
            return@withContext Result.failure(Exception("Reward misi sudah pernah diklaim"))
        }

        missionDao.markAsClaimed(missionId)
        walletDao.addPoints(mission.rewardPoints)

        historyDao.insertHistory(
            PointHistoryEntity(
                title = "Hadiah: ${mission.title}",
                pointsChange = mission.rewardPoints,
                isAddition = true
            )
        )

        Result.success(mission.rewardPoints)
    }

    suspend fun withdrawPoints(
        provider: String,
        phoneNumber: String,
        amountRupiah: Int,
        pointsRequired: Int
    ): Result<WithdrawalTransactionEntity> = withContext(Dispatchers.IO) {
        val currentWallet = walletDao.getUserWalletDirect() ?: return@withContext Result.failure(
            Exception("Dompet tidak ditemukan")
        )

        if (currentWallet.points < pointsRequired) {
            return@withContext Result.failure(Exception("Poin kamu tidak mencukupi untuk penukaran ini!"))
        }

        val refCode = "GC-" + (100000 + Random.nextInt(900000))
        val transaction = WithdrawalTransactionEntity(
            walletProvider = provider,
            phoneNumber = phoneNumber,
            amountRupiah = amountRupiah,
            pointsDeducted = pointsRequired,
            status = "Berhasil Dikonfirmasi",
            referenceCode = refCode
        )

        walletDao.deductPoints(pointsRequired)
        transactionDao.insertTransaction(transaction)

        historyDao.insertHistory(
            PointHistoryEntity(
                title = "Tukar $provider Rp ${"%,d".format(amountRupiah)}",
                pointsChange = -pointsRequired,
                isAddition = false
            )
        )

        Result.success(transaction)
    }
}

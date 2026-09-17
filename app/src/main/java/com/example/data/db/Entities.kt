package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_wallet")
data class UserWalletEntity(
    @PrimaryKey val id: Int = 1,
    val points: Int = 3500, // Initial welcome points for good onboarding experience
    val lastCheckInDate: String = "",
    val streakDays: Int = 1,
    val gamesPlayed: Int = 0,
    val minutesPlayed: Int = 0
)

@Entity(tableName = "daily_missions")
data class DailyMissionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val rewardPoints: Int,
    val targetCount: Int,
    val currentCount: Int,
    val isClaimed: Boolean = false,
    val iconName: String = "gamepad"
) {
    val isCompleted: Boolean
        get() = currentCount >= targetCount
}

@Entity(tableName = "withdrawal_transactions")
data class WithdrawalTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val walletProvider: String, // DANA, GOPAY, OVO, SHOPEEPAY
    val phoneNumber: String,
    val amountRupiah: Int,
    val pointsDeducted: Int,
    val status: String, // "Berhasil", "Diproses"
    val timestamp: Long = System.currentTimeMillis(),
    val referenceCode: String
)

@Entity(tableName = "point_history")
data class PointHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val pointsChange: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isAddition: Boolean = true
)

package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserWalletDao {
    @Query("SELECT * FROM user_wallet WHERE id = 1")
    fun getUserWallet(): Flow<UserWalletEntity?>

    @Query("SELECT * FROM user_wallet WHERE id = 1")
    suspend fun getUserWalletDirect(): UserWalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(wallet: UserWalletEntity)

    @Query("UPDATE user_wallet SET points = points + :addPoints WHERE id = 1")
    suspend fun addPoints(addPoints: Int)

    @Query("UPDATE user_wallet SET points = points - :deductPoints WHERE id = 1")
    suspend fun deductPoints(deductPoints: Int)

    @Query("UPDATE user_wallet SET gamesPlayed = gamesPlayed + 1 WHERE id = 1")
    suspend fun incrementGamesPlayed()

    @Query("UPDATE user_wallet SET minutesPlayed = minutesPlayed + :minutes WHERE id = 1")
    suspend fun addMinutesPlayed(minutes: Int)
}

@Dao
interface DailyMissionDao {
    @Query("SELECT * FROM daily_missions")
    fun getAllMissions(): Flow<List<DailyMissionEntity>>

    @Query("SELECT * FROM daily_missions WHERE id = :missionId")
    suspend fun getMissionById(missionId: String): DailyMissionEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertInitialMissions(missions: List<DailyMissionEntity>)

    @Update
    suspend fun updateMission(mission: DailyMissionEntity)

    @Query("UPDATE daily_missions SET currentCount = MIN(targetCount, currentCount + :amount) WHERE id = :missionId")
    suspend fun incrementProgress(missionId: String, amount: Int = 1)

    @Query("UPDATE daily_missions SET isClaimed = 1 WHERE id = :missionId")
    suspend fun markAsClaimed(missionId: String)
}

@Dao
interface WithdrawalTransactionDao {
    @Query("SELECT * FROM withdrawal_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<WithdrawalTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WithdrawalTransactionEntity)
}

@Dao
interface PointHistoryDao {
    @Query("SELECT * FROM point_history ORDER BY timestamp DESC LIMIT 50")
    fun getRecentHistory(): Flow<List<PointHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: PointHistoryEntity)
}

package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.PointHistoryEntity
import com.example.data.model.DanaKagetCampaign
import com.example.data.model.DanaKagetClaimResult
import com.example.notification.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class DanaKagetRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    private val prefs = context.getSharedPreferences("dana_kaget_prefs", Context.MODE_PRIVATE)

    private val _activeCampaign = MutableStateFlow<DanaKagetCampaign?>(null)
    val activeCampaign: StateFlow<DanaKagetCampaign?> = _activeCampaign.asStateFlow()

    private val _claimedCampaignIds = MutableStateFlow<Set<String>>(emptySet())
    val claimedCampaignIds: StateFlow<Set<String>> = _claimedCampaignIds.asStateFlow()

    init {
        loadStoredCampaign()
        loadClaimedIds()
    }

    private fun loadStoredCampaign() {
        val jsonStr = prefs.getString("active_campaign_json", null)
        if (!jsonStr.isNullOrBlank()) {
            try {
                val json = JSONObject(jsonStr)
                val claimedArray = json.optJSONArray("claimedByUsers") ?: JSONArray()
                val claimedList = mutableListOf<String>()
                for (i in 0 until claimedArray.length()) {
                    claimedList.add(claimedArray.getString(i))
                }

                val campaign = DanaKagetCampaign(
                    id = json.getString("id"),
                    title = json.getString("title"),
                    linkUrl = json.getString("linkUrl"),
                    totalQuota = json.getInt("totalQuota"),
                    remainingQuota = json.getInt("remainingQuota"),
                    rewardPointsPerUser = json.getInt("rewardPointsPerUser"),
                    createdAt = json.getLong("createdAt"),
                    isActive = json.getBoolean("isActive"),
                    claimedByUsers = claimedList
                )
                _activeCampaign.value = campaign
            } catch (_: Exception) {
                _activeCampaign.value = null
            }
        } else {
            // Seed a starter active Dana Kaget campaign so admin & user can see it right away!
            val starterCampaign = DanaKagetCampaign(
                id = "starter_drop_cuan",
                title = "🎁 DROP DANA KAGET SPESIAL KOMUNITAS",
                linkUrl = "https://link.dana.id/kaget?c=s9v8x4m2&r=gamecuan",
                totalQuota = 50,
                remainingQuota = 32, // Some claimed, exciting rush
                rewardPointsPerUser = 2500,
                createdAt = System.currentTimeMillis(),
                isActive = true,
                claimedByUsers = emptyList()
            )
            saveCampaignInternal(starterCampaign)
            _activeCampaign.value = starterCampaign
        }
    }

    private fun loadClaimedIds() {
        val set = prefs.getStringSet("claimed_campaign_ids", emptySet()) ?: emptySet()
        _claimedCampaignIds.value = set
    }

    private fun saveCampaignInternal(campaign: DanaKagetCampaign?) {
        if (campaign == null) {
            prefs.edit().remove("active_campaign_json").apply()
            _activeCampaign.value = null
            return
        }

        val json = JSONObject().apply {
            put("id", campaign.id)
            put("title", campaign.title)
            put("linkUrl", campaign.linkUrl)
            put("totalQuota", campaign.totalQuota)
            put("remainingQuota", campaign.remainingQuota)
            put("rewardPointsPerUser", campaign.rewardPointsPerUser)
            put("createdAt", campaign.createdAt)
            put("isActive", campaign.isActive)
            val arr = JSONArray()
            campaign.claimedByUsers.forEach { arr.put(it) }
            put("claimedByUsers", arr)
        }

        prefs.edit().putString("active_campaign_json", json.toString()).apply()
        _activeCampaign.value = campaign
    }

    /**
     * Admin broadcasts a new Dana Kaget link drop.
     * Triggers local system notification for users immediately!
     */
    suspend fun createAndBroadcastCampaign(
        title: String,
        linkUrl: String,
        totalQuota: Int,
        rewardPointsPerUser: Int,
        notifyUsers: Boolean = true
    ): DanaKagetCampaign = withContext(Dispatchers.IO) {
        val newCampaign = DanaKagetCampaign(
            id = "dk_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().take(6),
            title = if (title.isBlank()) "🎁 DANA KAGET DROP TERCEPAT!" else title.trim(),
            linkUrl = if (linkUrl.isBlank()) "https://link.dana.id/kaget" else linkUrl.trim(),
            totalQuota = totalQuota.coerceAtLeast(1),
            remainingQuota = totalQuota.coerceAtLeast(1),
            rewardPointsPerUser = rewardPointsPerUser.coerceAtLeast(100),
            createdAt = System.currentTimeMillis(),
            isActive = true,
            claimedByUsers = emptyList()
        )

        saveCampaignInternal(newCampaign)

        if (notifyUsers) {
            val notifTitle = "🔥 DANA KAGET BARU DIBUKA!"
            val notifMsg = "${newCampaign.title} - Kuota ${newCampaign.totalQuota} orang tercepat! Saldo rata +${newCampaign.rewardPointsPerUser} koin. Buruan klaim sekarang!"
            NotificationHelper.showNotification(
                context = context,
                title = notifTitle,
                message = notifMsg,
                notificationId = NotificationHelper.NOTIFICATION_ID_DANA_KAGET
            )
        }

        newCampaign
    }

    /**
     * Admin cancels or ends current Dana Kaget campaign
     */
    suspend fun endCampaign() = withContext(Dispatchers.IO) {
        val current = _activeCampaign.value ?: return@withContext
        val ended = current.copy(isActive = false, remainingQuota = 0)
        saveCampaignInternal(ended)
    }

    /**
     * User attempts to claim the Dana Kaget (Fastest-finger, equal distribution).
     */
    suspend fun claimDanaKaget(userId: String = "current_user"): DanaKagetClaimResult = withContext(Dispatchers.IO) {
        val campaign = _activeCampaign.value
            ?: return@withContext DanaKagetClaimResult(
                success = false,
                message = "Saat ini belum ada event Dana Kaget yang aktif. Nantikan drop berikutnya dari Admin!"
            )

        if (!campaign.isActive || campaign.remainingQuota <= 0) {
            return@withContext DanaKagetClaimResult(
                success = false,
                message = "Yah, kuota Dana Kaget sudah habis terisi (${campaign.totalQuota}/${campaign.totalQuota}). Siapa cepat dia dapat!",
                remainingQuota = 0
            )
        }

        // Check if this user already claimed
        val claimedSet = _claimedCampaignIds.value
        if (claimedSet.contains(campaign.id) || campaign.claimedByUsers.contains(userId)) {
            return@withContext DanaKagetClaimResult(
                success = false,
                message = "Kamu sudah berhasil mengklaim bagian Dana Kaget ini sebelumnya!",
                pointsAwarded = campaign.rewardPointsPerUser,
                linkUrl = campaign.linkUrl,
                remainingQuota = campaign.remainingQuota
            )
        }

        // Deduct quota by 1
        val newRemaining = (campaign.remainingQuota - 1).coerceAtLeast(0)
        val updatedClaimedUsers = campaign.claimedByUsers + userId
        val updatedCampaign = campaign.copy(
            remainingQuota = newRemaining,
            isActive = newRemaining > 0,
            claimedByUsers = updatedClaimedUsers
        )
        saveCampaignInternal(updatedCampaign)

        // Save user claim record locally
        val newClaimedSet = claimedSet + campaign.id
        prefs.edit().putStringSet("claimed_campaign_ids", newClaimedSet).apply()
        _claimedCampaignIds.value = newClaimedSet

        // Add reward points directly to user wallet & record history
        val walletDao = database.userWalletDao()
        val historyDao = database.pointHistoryDao()
        walletDao.addPoints(campaign.rewardPointsPerUser)
        historyDao.insertHistory(
            PointHistoryEntity(
                title = "Dana Kaget: ${campaign.title}",
                pointsChange = campaign.rewardPointsPerUser,
                isAddition = true
            )
        )

        DanaKagetClaimResult(
            success = true,
            message = "Selamat! Kamu kebagian kuota tercepat! +${campaign.rewardPointsPerUser} koin telah ditambahkan ke dompetmu.",
            pointsAwarded = campaign.rewardPointsPerUser,
            linkUrl = campaign.linkUrl,
            remainingQuota = newRemaining
        )
    }
}

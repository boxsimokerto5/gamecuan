package com.example.data.model

data class DanaKagetCampaign(
    val id: String,
    val title: String,
    val linkUrl: String,
    val totalQuota: Int,
    val remainingQuota: Int,
    val rewardPointsPerUser: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val claimedByUsers: List<String> = emptyList() // List of device/user IDs who claimed
)

data class DanaKagetClaimResult(
    val success: Boolean,
    val message: String,
    val pointsAwarded: Int = 0,
    val linkUrl: String = "",
    val remainingQuota: Int = 0
)

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.DailyMissionEntity
import com.example.data.db.UserWalletEntity
import com.example.notification.DailyReminderScheduler
import com.example.notification.NotificationHelper
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MissionsScreen(
    wallet: UserWalletEntity,
    missions: List<DailyMissionEntity>,
    onCheckInClicked: () -> Unit,
    onClaimMission: (String) -> Unit,
    onNavigateToGames: () -> Unit,
    onShareApp: () -> Unit,
    danaKagetCampaign: com.example.data.model.DanaKagetCampaign? = null,
    isDanaKagetClaimed: Boolean = false,
    onClaimDanaKaget: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isAlreadyCheckedIn = wallet.lastCheckInDate == today

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("missions_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dana Kaget Drop Banner (Active & Flash Rush)
        if (danaKagetCampaign != null) {
            item {
                com.example.ui.components.DanaKagetLiveBanner(
                    campaign = danaKagetCampaign,
                    isClaimedByMe = isDanaKagetClaimed,
                    onClaimClick = onClaimDanaKaget,
                    onOpenAdminPanel = null
                )
            }
        }

        // Daily Check-in Streak Card
        item {
            DailyCheckInCard(
                wallet = wallet,
                isAlreadyCheckedIn = isAlreadyCheckedIn,
                onCheckInClicked = onCheckInClicked
            )
        }

        // Daily Reminder Card for Check-in & Missions
        item {
            DailyReminderCard()
        }

        // Quick Stats row
        item {
            QuickStatsRow(wallet = wallet)
        }

        // Missions Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Misi Harian Cuan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Selesaikan misi untuk menumpuk poin dan tukar saldo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }

                val completedMissions = missions.count { it.isCompleted }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "$completedMissions/${missions.size} Selesai",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Mission Items
        items(missions, key = { it.id }) { mission ->
            MissionCardItem(
                mission = mission,
                onClaim = { onClaimMission(mission.id) },
                onAction = {
                    when (mission.id) {
                        "share_app" -> onShareApp()
                        "daily_checkin" -> onCheckInClicked()
                        else -> onNavigateToGames()
                    }
                }
            )
        }
    }
}

@Composable
private fun DailyCheckInCard(
    wallet: UserWalletEntity,
    isAlreadyCheckedIn: Boolean,
    onCheckInClicked: () -> Unit
) {
    val streakRewards = listOf(150, 250, 350, 500, 750, 1000, 2000)
    val currentStreak = wallet.streakDays

    // Calculate which day is active to claim today
    val nextDayToClaim = if (isAlreadyCheckedIn) {
        currentStreak.coerceIn(1, 7)
    } else {
        if (currentStreak >= 7) 1 else (currentStreak + 1).coerceIn(1, 7)
    }

    val todayReward = streakRewards[nextDayToClaim - 1]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("checkin_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with responsive weight
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GoldCoin.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldCoin,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Streak Check-in Harian",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Klaim berturut-turut hingga hari ke-7!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    color = GoldCoin.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🔥 ${wallet.streakDays} Hari",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GoldCoinDark,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Row 1: Hari 1 - 4 (100% fit, no horizontal cutoff)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (dayNum in 1..4) {
                    val reward = streakRewards[dayNum - 1]
                    val isPast = if (isAlreadyCheckedIn) dayNum <= currentStreak else dayNum < nextDayToClaim
                    val isCurrent = !isAlreadyCheckedIn && dayNum == nextDayToClaim

                    DailyCheckInDayItem(
                        dayNum = dayNum,
                        reward = reward,
                        isPast = isPast,
                        isCurrent = isCurrent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Hari 5 - 7 (Hari 7 is Grand Reward / Jackpot)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (dayNum in 5..6) {
                    val reward = streakRewards[dayNum - 1]
                    val isPast = if (isAlreadyCheckedIn) dayNum <= currentStreak else dayNum < nextDayToClaim
                    val isCurrent = !isAlreadyCheckedIn && dayNum == nextDayToClaim

                    DailyCheckInDayItem(
                        dayNum = dayNum,
                        reward = reward,
                        isPast = isPast,
                        isCurrent = isCurrent,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Hari 7 Grand Reward Card
                val day7Reward = streakRewards[6]
                val isPast7 = if (isAlreadyCheckedIn) 7 <= currentStreak else 7 < nextDayToClaim
                val isCurrent7 = !isAlreadyCheckedIn && 7 == nextDayToClaim

                DailyCheckInDay7SpecialItem(
                    reward = day7Reward,
                    isPast = isPast7,
                    isCurrent = isCurrent7,
                    modifier = Modifier.weight(1.35f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check-in Action Button
            Button(
                onClick = onCheckInClicked,
                enabled = !isAlreadyCheckedIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("checkin_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.18f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isAlreadyCheckedIn) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sudah Check-in Hari Ini ✓ (Kembali Besok)",
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = GoldCoin,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Klaim Check-in Hari ke-$nextDayToClaim (+${todayReward} Poin)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyCheckInDayItem(
    dayNum: Int,
    reward: Int,
    isPast: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isPast -> SuccessGreen.copy(alpha = 0.14f)
                    isCurrent -> GoldCoin.copy(alpha = 0.22f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                }
            )
            .border(
                width = if (isCurrent) 1.5.dp else 0.dp,
                color = if (isCurrent) GoldCoin else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Text(
            text = "H-$dayNum",
            fontSize = 11.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
            color = if (isCurrent) GoldCoinDark else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isPast) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Sudah diklaim",
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = if (isCurrent) GoldCoin else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isPast) "Klaim ✓" else "+$reward",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = when {
                isPast -> SuccessGreen
                isCurrent -> GoldCoinDark
                else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            }
        )
    }
}

@Composable
private fun DailyCheckInDay7SpecialItem(
    reward: Int,
    isPast: Boolean,
    isCurrent: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isPast -> SuccessGreen.copy(alpha = 0.16f)
                    isCurrent -> GoldCoin.copy(alpha = 0.28f)
                    else -> GoldCoin.copy(alpha = 0.12f)
                }
            )
            .border(
                width = if (isCurrent) 2.dp else 1.dp,
                color = if (isCurrent) GoldCoin else GoldCoin.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(vertical = 10.dp, horizontal = 4.dp)
    ) {
        Text(
            text = "Hari 7 🔥",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isPast) SuccessGreen else GoldCoinDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isPast) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Hadiah Utama Diklaim",
                tint = SuccessGreen,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.MonetizationOn,
                contentDescription = null,
                tint = GoldCoin,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = if (isPast) "Jackpot ✓" else "+$reward",
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (isPast) SuccessGreen else GoldCoinDark
        )
    }
}

@Composable
private fun QuickStatsRow(wallet: UserWalletEntity) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatPill(
            icon = Icons.Default.SportsEsports,
            label = "Game Dimainkan",
            value = "${wallet.gamesPlayed}",
            modifier = Modifier.weight(1f)
        )
        StatPill(
            icon = Icons.Default.Timer,
            label = "Total Durasi",
            value = "${wallet.minutesPlayed} Menit",
            modifier = Modifier.weight(1f)
        )
        StatPill(
            icon = Icons.Default.MonetizationOn,
            label = "Status Cuan",
            value = "Aktif",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatPill(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun MissionCardItem(
    mission: DailyMissionEntity,
    onClaim: () -> Unit,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mission_item_${mission.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mission Icon Box
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            when (mission.iconName) {
                                "timer" -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                                "explore" -> Color(0xFF06B6D4).copy(alpha = 0.15f)
                                "share" -> Color(0xFFEC4899).copy(alpha = 0.15f)
                                else -> BrandPrimary.copy(alpha = 0.15f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val iconVector = when (mission.iconName) {
                        "timer" -> Icons.Default.Timer
                        "explore" -> Icons.Default.Explore
                        "share" -> Icons.Default.Share
                        else -> Icons.Default.SportsEsports
                    }
                    val iconTint = when (mission.iconName) {
                        "timer" -> Color(0xFF8B5CF6)
                        "explore" -> Color(0xFF06B6D4)
                        "share" -> Color(0xFFEC4899)
                        else -> BrandPrimary
                    }
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and progress description
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = mission.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = mission.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Reward Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldCoin.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${mission.rewardPoints}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = GoldCoinDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress & Claim/Go Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Progress Bar
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Progres: ${mission.currentCount}/${mission.targetCount}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        if (mission.isCompleted && !mission.isClaimed) {
                            Text(
                                text = "Siap Diklaim!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    val progressRatio = (mission.currentCount.toFloat() / mission.targetCount.coerceAtLeast(1)).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progressRatio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (mission.isCompleted) SuccessGreen else BrandPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Action button depending on status
                when {
                    mission.isClaimed -> {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Diklaim",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                    mission.isCompleted -> {
                        Button(
                            onClick = onClaim,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GoldCoin
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("claim_btn_${mission.id}"),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Klaim",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                        }
                    }
                    else -> {
                        OutlinedButton(
                            onClick = onAction,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(36.dp)
                                .testTag("action_btn_${mission.id}"),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Mulai",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyReminderCard() {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(DailyReminderScheduler.isReminderEnabled(context)) }
    val initialTime = remember { DailyReminderScheduler.getReminderTime(context) }
    var selectedHour by remember { mutableIntStateOf(initialTime.first) }
    var testStatusText by remember { mutableStateOf<String?>(null) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("daily_reminder_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isEnabled) GoldCoin.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = "Pengingat Harian",
                                tint = if (isEnabled) GoldCoinDark else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Pengingat Harian",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (isEnabled) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = SuccessGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Aktif",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (isEnabled) {
                                "Diingatkan pukul ${String.format(Locale.getDefault(), "%02d:00", selectedHour)} WIB jika belum check-in/selesai misi"
                            } else {
                                "Pengingat dinonaktifkan"
                            },
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            lineHeight = 16.sp
                        )
                    }
                }

                Switch(
                    checked = isEnabled,
                    onCheckedChange = { checked ->
                        isEnabled = checked
                        DailyReminderScheduler.setReminderEnabled(context, checked)
                        testStatusText = if (checked) {
                            "Pengingat aktif setiap ${String.format(Locale.getDefault(), "%02d:00", selectedHour)} WIB"
                        } else {
                            "Pengingat dinonaktifkan"
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BrandPrimary
                    ),
                    modifier = Modifier.testTag("reminder_switch")
                )
            }

            AnimatedVisibility(visible = isEnabled) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "Pilih Waktu Pengingat:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(18, 19, 20, 21).forEach { hour ->
                            val isSelected = selectedHour == hour
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedHour = hour
                                    DailyReminderScheduler.setReminderTime(context, hour, 0)
                                    testStatusText = "Waktu diubah ke ${String.format(Locale.getDefault(), "%02d:00", hour)} WIB"
                                },
                                label = {
                                    Text(
                                        text = "${hour}:00",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = BrandPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = {
                                DailyReminderScheduler.checkAndSendReminderIfPending(
                                    context = context,
                                    isTest = true
                                ) { _, msg ->
                                    testStatusText = msg
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("test_notification_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tes Notifikasi Sekarang",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        testStatusText?.let { status ->
                            Text(
                                text = status,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                maxLines = 1,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


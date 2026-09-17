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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.DailyMissionEntity
import com.example.data.db.UserWalletEntity
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
        // Daily Check-in Streak Card
        item {
            DailyCheckInCard(
                wallet = wallet,
                isAlreadyCheckedIn = isAlreadyCheckedIn,
                onCheckInClicked = onCheckInClicked
            )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldCoin.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = GoldCoin,
                            modifier = Modifier.size(20.dp)
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
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Surface(
                    color = GoldCoin.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "🔥 ${wallet.streakDays} Hari",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GoldCoinDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 7 Days Reward Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(7) { index ->
                    val dayNum = index + 1
                    val reward = streakRewards[index]
                    val isPast = dayNum < wallet.streakDays || (dayNum == wallet.streakDays && isAlreadyCheckedIn)
                    val isCurrent = dayNum == wallet.streakDays && !isAlreadyCheckedIn

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                when {
                                    isPast -> SuccessGreen.copy(alpha = 0.12f)
                                    isCurrent -> GoldCoin.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .border(
                                width = if (isCurrent) 1.5.dp else 0.dp,
                                color = if (isCurrent) GoldCoin else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = "H-$dayNum",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isPast) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Sudah diambil",
                                tint = SuccessGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = "+$reward",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCurrent) GoldCoinDark else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Check-in Button
            Button(
                onClick = onCheckInClicked,
                enabled = !isAlreadyCheckedIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("checkin_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandPrimary,
                    disabledContainerColor = SuccessGreen.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isAlreadyCheckedIn) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sudah Check-in Hari Ini ✓",
                        color = SuccessGreen,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Check-in Hari Ini (Klaim Bonus Poin)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
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

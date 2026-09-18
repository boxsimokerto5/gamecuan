package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserWalletEntity
import com.example.data.db.WithdrawalTransactionEntity
import com.example.data.model.DanaKagetCampaign
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DanaBlue
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    adminUsername: String,
    activeCampaign: DanaKagetCampaign?,
    userWallet: UserWalletEntity,
    transactions: List<WithdrawalTransactionEntity>,
    onBroadcastDanaKaget: (title: String, linkUrl: String, quota: Int, rewardPoints: Int) -> Unit,
    onEndDanaKaget: () -> Unit,
    onLogoutAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dana Kaget, 1: Penarikan Pemain, 2: Statistik Game

    var titleInput by remember(activeCampaign) {
        mutableStateOf(activeCampaign?.title ?: "🎁 DANA KAGET DROP SPESIAL!")
    }
    var linkInput by remember(activeCampaign) {
        mutableStateOf(activeCampaign?.linkUrl ?: "https://link.dana.id/kaget?")
    }
    var quotaInput by remember(activeCampaign) {
        mutableStateOf(activeCampaign?.totalQuota?.toString() ?: "50")
    }
    var rewardInput by remember(activeCampaign) {
        mutableStateOf(activeCampaign?.rewardPointsPerUser?.toString() ?: "2000")
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(Color(0xFFE11D48), Color(0xFF9333EA)))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "PANEL ADMIN",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = SuccessGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "SUPERUSER",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Login sebagai: $adminUsername",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }

                        // Logout Admin button to return to regular player mode
                        Button(
                            onClick = onLogoutAdmin,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE11D48)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("admin_logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Keluar Mode Admin",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Keluar Admin",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color.White,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Sebar Dana Kaget", fontSize = 12.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Tukar Saldo (${transactions.size})", fontSize = 12.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Statistik Sistem", fontSize = 12.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A))
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Current Status Card
                    item {
                        AdminDanaKagetLiveStatus(
                            campaign = activeCampaign,
                            onEndCampaign = onEndDanaKaget
                        )
                    }

                    // Form Broadcast
                    item {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            modifier = Modifier.fillMaxWidth().testTag("admin_broadcast_form_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.RocketLaunch,
                                        contentDescription = null,
                                        tint = DanaBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Formulir Sebar Dana Kaget Baru",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "Pemain akan otomatis menerima notifikasi instan dan berebut kuota tercepat dengan sistem bagi rata.",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.65f),
                                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                                )

                                OutlinedTextField(
                                    value = titleInput,
                                    onValueChange = { titleInput = it },
                                    label = { Text("Judul Program / Event", color = Color.White.copy(alpha = 0.7f)) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = DanaBlue,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("admin_form_title")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = linkInput,
                                    onValueChange = { linkInput = it },
                                    label = { Text("Tautan Link Resmi DANA Kaget", color = Color.White.copy(alpha = 0.7f)) },
                                    placeholder = { Text("https://link.dana.id/kaget?...", color = Color.Gray) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        focusedBorderColor = DanaBlue,
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth().testTag("admin_form_link")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = quotaInput,
                                        onValueChange = { quotaInput = it.filter { c -> c.isDigit() } },
                                        label = { Text("Total Kuota", color = Color.White.copy(alpha = 0.7f)) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = DanaBlue,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                        ),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("admin_form_quota")
                                    )

                                    OutlinedTextField(
                                        value = rewardInput,
                                        onValueChange = { rewardInput = it.filter { c -> c.isDigit() } },
                                        label = { Text("Koin Bagi Rata", color = Color.White.copy(alpha = 0.7f)) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White,
                                            focusedBorderColor = DanaBlue,
                                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f)
                                        ),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f).testTag("admin_form_reward")
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val isValid = titleInput.isNotBlank() &&
                                        linkInput.isNotBlank() &&
                                        (quotaInput.toIntOrNull() ?: 0) > 0 &&
                                        (rewardInput.toIntOrNull() ?: 0) > 0

                                Button(
                                    onClick = {
                                        val q = quotaInput.toIntOrNull() ?: 50
                                        val r = rewardInput.toIntOrNull() ?: 2000
                                        onBroadcastDanaKaget(titleInput.trim(), linkInput.trim(), q, r)
                                    },
                                    enabled = isValid,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = DanaBlue),
                                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("admin_submit_broadcast_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "SIARKAN DANA KAGET KE PEMAIN SEKARANG",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    item {
                        Text(
                            text = "Daftar Permintaan Penarikan Saldo Pemain",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Semua riwayat transaksi pencairan ke DANA, GoPay, dan OVO tersimpan di database lokal.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    if (transactions.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Belum ada penarikan saldo pemain yang tercatat.", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                                }
                            }
                        }
                    } else {
                        items(transactions) { tx ->
                            val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(tx.timestamp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(tx.provider, fontWeight = FontWeight.Black, fontSize = 14.sp, color = DanaBlue)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = SuccessGreen.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = tx.status,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = SuccessGreen,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(text = "No: ${tx.phoneNumber}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text(text = "Ref: ${tx.referenceCode} • $dateStr", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "Rp ${"%,d".format(tx.amountRupiah)}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp,
                                            color = SuccessGreen
                                        )
                                        Text(
                                            text = "-${"%,d".format(tx.pointsDeducted)} Poin",
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Text("Ringkasan Ekosistem Game Cuan", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("Total Main Game", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                            Text("${userWallet.gamesPlayed} Kali", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GoldCoin)
                                        }
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFF0F172A),
                                        modifier = Modifier.weight(1f).padding(vertical = 4.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text("Waktu Bermain", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                            Text("${userWallet.minutesPlayed} Menit", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF0F172A),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text("Database Lokal (Room DB)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                            Text("Aktif & Tersinkronisasi", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDanaKagetLiveStatus(
    campaign: DanaKagetCampaign?,
    onEndCampaign: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth().testTag("admin_live_status_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (campaign != null && campaign.isActive) SuccessGreen else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (campaign != null && campaign.isActive) "EVENT DANA KAGET SEDANG BERJALAN" else "TIDAK ADA EVENT BERJALAN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (campaign != null && campaign.isActive) SuccessGreen else Color.LightGray
                    )
                }

                if (campaign != null && campaign.isActive) {
                    OutlinedButton(
                        onClick = onEndCampaign,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE11D48)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Akhiri Event", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (campaign != null && campaign.isActive) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = campaign.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
                Text(
                    text = "Link: ${campaign.linkUrl}",
                    fontSize = 11.sp,
                    color = DanaBlue,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(10.dp))

                val claimedCount = (campaign.totalQuota - campaign.remainingQuota).coerceAtLeast(0)
                val progress = if (campaign.totalQuota > 0) claimedCount.toFloat() / campaign.totalQuota.toFloat() else 0f

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Terklaim: $claimedCount dari ${campaign.totalQuota} pemain", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    Text("Sisa: ${campaign.remainingQuota}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldCoin)
                }

                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = DanaBlue,
                    trackColor = Color(0xFF334155)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Hadiah bagi rata: ${"%,d".format(campaign.rewardPointsPerUser)} koin/pemain",
                    fontSize = 11.sp,
                    color = SuccessGreen
                )
            }
        }
    }
}

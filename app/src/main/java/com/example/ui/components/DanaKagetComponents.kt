package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DanaKagetCampaign
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.DanaBlue
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.SuccessGreen

@Composable
fun DanaKagetLiveBanner(
    campaign: DanaKagetCampaign?,
    isClaimedByMe: Boolean,
    onClaimClick: () -> Unit,
    onOpenAdminPanel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (campaign == null) return

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showResultDialog by remember { mutableStateOf(false) }
    var claimDialogTitle by remember { mutableStateOf("") }
    var claimDialogMessage by remember { mutableStateOf("") }
    var claimDialogPoints by remember { mutableStateOf(0) }
    var claimDialogLink by remember { mutableStateOf<String?>(null) }
    var isClaimSuccess by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_lightning")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val progress = if (campaign.totalQuota > 0) {
        campaign.remainingQuota.toFloat() / campaign.totalQuota.toFloat()
    } else 0f

    val isAvailable = campaign.isActive && campaign.remainingQuota > 0

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dana_kaget_banner")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF0F172A), // Midnight Dark
                            Color(0xFF1E293B),
                            Color(0xFF111827)
                        )
                    )
                )
                .border(
                    width = if (isAvailable) 2.dp else 1.dp,
                    brush = if (isAvailable) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFF59E0B),
                                DanaBlue
                            )
                        )
                    } else {
                        Brush.linearGradient(listOf(Color(0xFF475569), Color(0xFF334155)))
                    },
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isAvailable) Color(0xFFFF4500).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = if (isAvailable) Color(0xFFFF4500) else Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAvailable) "FLASH DROP" else "HABIS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isAvailable) Color(0xFFFF6B4A) else Color.LightGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = DanaBlue.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "DANA KAGET",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DanaBlue
                                )
                            }
                        }
                    }

                    // Admin Settings shortcut (only visible if admin callback passed)
                    if (onOpenAdminPanel != null) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.1f),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .clickable { onOpenAdminPanel() }
                                .testTag("open_admin_kaget_btn")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Panel Admin",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Title & Subtitle
                Text(
                    text = campaign.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = GoldCoin,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Bagi Rata: +${"%,d".format(campaign.rewardPointsPerUser)} Koin / Orang",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldCoin
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quota Progress Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAvailable) "Sisa ${campaign.remainingQuota} kuota lagi!" else "Semua kuota telah terisi",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAvailable) Color.White.copy(alpha = 0.9f) else Color.Gray
                        )
                        Text(
                            text = "${campaign.totalQuota - campaign.remainingQuota} / ${campaign.totalQuota} Terklaim",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isAvailable) GoldCoin else Color.Gray,
                        trackColor = Color.White.copy(alpha = 0.15f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Claim Button / Status
                when {
                    isClaimedByMe -> {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SuccessGreen.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Kamu Sudah Berhasil Klaim!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }

                                if (campaign.linkUrl.isNotBlank()) {
                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(campaign.linkUrl))
                                                context.startActivity(browserIntent)
                                            } catch (_: Exception) {
                                                clipboardManager.setText(AnnotatedString(campaign.linkUrl))
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DanaBlue)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.OpenInBrowser,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Buka Link", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    !isAvailable -> {
                        Button(
                            onClick = {},
                            enabled = false,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFF334155),
                                disabledContentColor = Color.LightGray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Yah, Kuota Habis! Tunggu Drop Berikutnya", fontSize = 13.sp)
                        }
                    }

                    else -> {
                        Button(
                            onClick = onClaimClick,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .scale(pulseScale)
                                .testTag("claim_dana_kaget_button")
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                Color(0xFFFF9100),
                                                Color(0xFFFFD700),
                                                DanaBlue
                                            )
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "KLAIM CEPAT SEKARANG! 🔥",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color.Black
                                    )
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
fun AdminDanaKagetDialog(
    currentCampaign: DanaKagetCampaign?,
    onDismiss: () -> Unit,
    onBroadcast: (title: String, link: String, quota: Int, rewardPoints: Int) -> Unit,
    onEndCampaign: () -> Unit
) {
    var adminPin by remember { mutableStateOf("") }
    var isAuthenticated by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf(currentCampaign?.title ?: "🎁 DANA KAGET DROP SPESIAL!") }
    var linkInput by remember { mutableStateOf(currentCampaign?.linkUrl ?: "https://link.dana.id/kaget?") }
    var quotaInput by remember { mutableStateOf(currentCampaign?.totalQuota?.toString() ?: "50") }
    var pointsInput by remember { mutableStateOf(currentCampaign?.rewardPointsPerUser?.toString() ?: "2000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAuthenticated) "Admin: Sebar Dana Kaget" else "Autentikasi Admin",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            if (!isAuthenticated) {
                Column {
                    Text(
                        text = "Masukkan PIN Admin untuk membuat dan menyebarkan program Dana Kaget ke seluruh pemain:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = adminPin,
                        onValueChange = {
                            adminPin = it
                            pinError = false
                        },
                        label = { Text("PIN Admin (Bawaan: 1234)") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        isError = pinError,
                        modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                    )
                    if (pinError) {
                        Text(
                            text = "PIN salah! Gunakan 1234 atau hubungi developer.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Isi detail link dan kuota. Notifikasi langsung dikirimkan ke perangkat pemain saat tombol sebar ditekan!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Judul Pengumuman") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_title_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = linkInput,
                        onValueChange = { linkInput = it },
                        label = { Text("Link Dana Kaget (DANA URL)") },
                        placeholder = { Text("https://link.dana.id/kaget?...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("admin_link_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = quotaInput,
                            onValueChange = { quotaInput = it.filter { char -> char.isDigit() } },
                            label = { Text("Kuota Pemenang") },
                            placeholder = { Text("50") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("admin_quota_input")
                        )

                        OutlinedTextField(
                            value = pointsInput,
                            onValueChange = { pointsInput = it.filter { char -> char.isDigit() } },
                            label = { Text("Koin Bagi Rata") },
                            placeholder = { Text("2000") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("admin_points_input")
                        )
                    }

                    if (currentCampaign != null && currentCampaign.isActive) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                onEndCampaign()
                                onDismiss()
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Akhiri Event Dana Kaget Saat Ini")
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isAuthenticated) {
                Button(
                    onClick = {
                        if (adminPin == "1234" || adminPin == "admin") {
                            isAuthenticated = true
                            pinError = false
                        } else {
                            pinError = true
                        }
                    },
                    modifier = Modifier.testTag("admin_auth_submit")
                ) {
                    Text("Masuk Admin")
                }
            } else {
                Button(
                    onClick = {
                        val quota = quotaInput.toIntOrNull() ?: 50
                        val points = pointsInput.toIntOrNull() ?: 2000
                        onBroadcast(titleInput, linkInput, quota, points)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    modifier = Modifier.testTag("admin_broadcast_submit")
                ) {
                    Text("🚀 Sebarkan Dana Kaget Sekarang!")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun DanaKagetClaimResultDialog(
    isSuccess: Boolean,
    message: String,
    pointsAwarded: Int,
    linkUrl: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (isSuccess) "🎉" else "ℹ️", fontSize = 22.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isSuccess) "Dana Kaget Didapat!" else "Status Dana Kaget",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )

                if (isSuccess && pointsAwarded > 0) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldCoin.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldCoinDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "+${"%,d".format(pointsAwarded)} Koin telah ditambahkan ke dompet akunmu!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = GoldCoinDark
                            )
                        }
                    }
                }

                if (!linkUrl.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Tautan Resmi DANA Kaget:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                                context.startActivity(intent)
                            } catch (_: Exception) {
                                clipboardManager.setText(AnnotatedString(linkUrl))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = DanaBlue)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buka Tautan di Aplikasi DANA / Browser", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                Text("Tutup")
            }
        }
    )
}

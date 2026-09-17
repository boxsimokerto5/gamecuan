package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.PointHistoryEntity
import com.example.data.db.UserWalletEntity
import com.example.data.db.WithdrawalTransactionEntity
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandPrimaryDark
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.DanaBlue
import com.example.ui.theme.GoPayCyan
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.OvoPurple
import com.example.ui.theme.ShopeeOrange
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EWalletProvider(
    val id: String,
    val name: String,
    val brandColor: Color,
    val tag: String? = null
)

@Composable
fun WalletScreen(
    wallet: UserWalletEntity,
    transactions: List<WithdrawalTransactionEntity>,
    pointHistory: List<PointHistoryEntity>,
    onWithdraw: (provider: String, phone: String, amountRupiah: Int, pointsRequired: Int, onSuccess: () -> Unit) -> Unit,
    userEmail: String? = null,
    isEmailConfirmed: Boolean = false,
    isSupabaseConfigured: Boolean = false,
    onOpenAuthDialog: () -> Unit = {},
    onOpenConfigDialog: () -> Unit = {},
    onSyncNow: () -> Unit = {},
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val providers = remember {
        listOf(
            EWalletProvider("DANA", "DANA", DanaBlue, "Populer"),
            EWalletProvider("GOPAY", "GoPay", GoPayCyan),
            EWalletProvider("OVO", "OVO", OvoPurple),
            EWalletProvider("SHOPEEPAY", "ShopeePay", ShopeeOrange)
        )
    }

    val denominations = remember {
        listOf(5000, 10000, 25000, 50000, 100000)
    }

    var selectedProvider by remember { mutableStateOf(providers[0]) }
    var selectedAmount by remember { mutableIntStateOf(denominations[0]) }
    var phoneNumber by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedHistoryTab by remember { mutableIntStateOf(0) } // 0: Penarikan, 1: Mutasi Poin

    val isSufficientPoints = wallet.points >= selectedAmount
    val isValidPhone = phoneNumber.length in 10..14 && phoneNumber.all { it.isDigit() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("wallet_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("balance_card"),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BrandPrimary, BrandSecondary, BrandPrimaryDark)
                            )
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Total Saldo Poin",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "1 Poin = Rp 1",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Points Counter
                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = GoldCoin,
                                modifier = Modifier
                                    .size(34.dp)
                                    .padding(bottom = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "%,d".format(wallet.points),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Poin",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Rupiah Equivalent
                        Text(
                            text = "Setara dengan Rp ${"%,d".format(wallet.points)} Saldo E-Wallet",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚡ Penarikan Langsung & Terpercaya",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                            Text(
                                text = "Bebas Biaya Admin",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldCoin
                            )
                        }
                    }
                }
            }
        }

        // Supabase Cloud Sync Card
        item {
            com.example.ui.components.SupabaseStatusCard(
                isConfigured = isSupabaseConfigured,
                userEmail = userEmail,
                isEmailConfirmed = isEmailConfirmed,
                onOpenAuthDialog = onOpenAuthDialog,
                onOpenConfigDialog = onOpenConfigDialog,
                onSyncNow = onSyncNow,
                onSignOut = onSignOut
            )
        }

        // Section: Penukaran Saldo
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Tukar Poin ke E-Wallet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pilih e-wallet tujuan dan nominal penukaran",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // E-Wallet Options
                    Text(
                        text = "1. Pilih E-Wallet Tujuan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        providers.forEach { provider ->
                            val isSelected = selectedProvider.id == provider.id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedProvider = provider }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) provider.brandColor else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .testTag("provider_choice_${provider.id}"),
                                color = if (isSelected) provider.brandColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(provider.brandColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = provider.name.take(1),
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 15.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = provider.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) provider.brandColor else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Denominations
                    Text(
                        text = "2. Pilih Nominal Penukaran",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(denominations) { amount ->
                            val isSelected = selectedAmount == amount
                            val canAfford = wallet.points >= amount
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedAmount = amount }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .testTag("amount_choice_$amount"),
                                color = if (isSelected) BrandPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Rp ${"%,d".format(amount)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${"%,d".format(amount)} Poin",
                                        fontSize = 11.sp,
                                        color = if (canAfford) SuccessGreen else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Phone Number Input
                    Text(
                        text = "3. Nomor HP Akun ${selectedProvider.name}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 14) {
                                phoneNumber = input
                            }
                        },
                        placeholder = { Text("Contoh: 081234567890", fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = null,
                                tint = selectedProvider.brandColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("phone_number_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedProvider.brandColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isSufficientPoints) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Poinmu kurang ${"%,d".format(selectedAmount - wallet.points)} poin untuk nominal ini. Selesaikan misi game untuk menambah poin!",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Withdraw Button
                    Button(
                        onClick = { showConfirmDialog = true },
                        enabled = isSufficientPoints && isValidPhone,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("withdraw_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandPrimary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "Tukarkan Rp ${"%,d".format(selectedAmount)} Sekarang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSufficientPoints && isValidPhone) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        // Section: History
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Riwayat Aktivitas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TabRow(
                        selectedTabIndex = selectedHistoryTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedHistoryTab]),
                                color = BrandPrimary
                            )
                        }
                    ) {
                        Tab(
                            selected = selectedHistoryTab == 0,
                            onClick = { selectedHistoryTab = 0 },
                            text = {
                                Text(
                                    "Penarikan Saldo (${transactions.size})",
                                    fontWeight = if (selectedHistoryTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedHistoryTab == 1,
                            onClick = { selectedHistoryTab = 1 },
                            text = {
                                Text(
                                    "Mutasi Poin (${pointHistory.size})",
                                    fontWeight = if (selectedHistoryTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedHistoryTab == 0) {
                        if (transactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada transaksi penarikan saldo",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            transactions.forEach { tx ->
                                TransactionHistoryItem(transaction = tx)
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    } else {
                        if (pointHistory.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Belum ada riwayat mutasi poin",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        } else {
                            pointHistory.forEach { item ->
                                PointMutationItem(history = item)
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Penukaran Saldo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pastikan data berikut sudah sesuai sebelum memproses:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    DetailRow(label = "E-Wallet", value = selectedProvider.name)
                    DetailRow(label = "Nomor Tujuan", value = phoneNumber)
                    DetailRow(label = "Nominal", value = "Rp ${"%,d".format(selectedAmount)}")
                    DetailRow(label = "Poin Terpotong", value = "${"%,d".format(selectedAmount)} Poin")
                    DetailRow(label = "Biaya Admin", value = "Gratis (Rp 0)")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        onWithdraw(
                            selectedProvider.name,
                            phoneNumber,
                            selectedAmount,
                            selectedAmount
                        ) {
                            phoneNumber = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    modifier = Modifier.testTag("confirm_withdraw_button")
                ) {
                    Text("Konfirmasi & Tukar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
private fun TransactionHistoryItem(transaction: WithdrawalTransactionEntity) {
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID")).format(Date(transaction.timestamp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = transaction.walletProvider,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SuccessGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = transaction.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SuccessGreen,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${transaction.phoneNumber} • ${transaction.referenceCode}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = dateStr,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Rp ${"%,d".format(transaction.amountRupiah)}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SuccessGreen
            )
            Text(
                text = "-${"%,d".format(transaction.pointsDeducted)} Poin",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun PointMutationItem(history: PointHistoryEntity) {
    val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale("id", "ID")).format(Date(history.timestamp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (history.isAddition) SuccessGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (history.isAddition) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (history.isAddition) SuccessGreen else Color.Red,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = history.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }

        Text(
            text = "${if (history.isAddition) "+" else ""}${"%,d".format(history.pointsChange)} Poin",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (history.isAddition) SuccessGreen else Color.Red
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

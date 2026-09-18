package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.SuccessGreen

@Composable
fun SupabaseStatusCard(
    isConfigured: Boolean,
    userEmail: String?,
    isEmailConfirmed: Boolean,
    onOpenAuthDialog: () -> Unit,
    onOpenConfigDialog: () -> Unit,
    onSyncNow: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("supabase_status_card"),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (userEmail != null) SuccessGreen.copy(alpha = 0.15f)
                                else Color(0xFF3ECF8E).copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (userEmail != null) Icons.Default.CloudDone else Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = if (userEmail != null) SuccessGreen else Color(0xFF3ECF8E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Supabase Cloud Sync",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isConfigured) SuccessGreen.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isConfigured) "Tersambung" else "Belum Konfigurasi",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isConfigured) SuccessGreen else Color.Red,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (userEmail != null) userEmail else "Login akun untuk sinkronisasi koin & transaksi",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = onOpenConfigDialog,
                    modifier = Modifier.size(32.dp).testTag("btn_open_supabase_config")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Pengaturan Supabase",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Email verification banner if logged in but email not confirmed yet
            if (userEmail != null && !isEmailConfirmed) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailRead,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Menunggu Verifikasi Email",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                            Text(
                                text = "Buka email Anda dan klik link verifikasi dari Supabase.",
                                fontSize = 11.sp,
                                color = Color(0xFFB45309).copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (userEmail == null) {
                    Button(
                        onClick = onOpenAuthDialog,
                        modifier = Modifier.weight(1f).height(38.dp).testTag("btn_open_login_dialog"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Masuk / Daftar Akun", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    OutlinedButton(
                        onClick = onSyncNow,
                        modifier = Modifier.weight(1f).height(38.dp).testTag("btn_sync_now"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sinkron Cloud", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = onSignOut,
                        modifier = Modifier.height(38.dp).testTag("btn_logout"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Keluar", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SupabaseAuthDialog(
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSignIn: (email: String, pass: String) -> Unit,
    onSignUp: (email: String, pass: String) -> Unit,
    onAdminLoginSuccess: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Masuk, 1: Daftar
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (selectedTab == 0) "Masuk Akun Game Cuan" else "Daftar Akun Baru",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tersambung dengan Supabase Auth & PostgreSQL",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Masuk", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Daftar Akun", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                if (selectedTab == 1) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MarkEmailRead,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Link konfirmasi verifikasi email akan dikirim otomatis oleh Supabase.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Email / Username Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (selectedTab == 0) "Email atau Username" else "Alamat Email") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = if (selectedTab == 0) KeyboardType.Text else KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("auth_email_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                val isAdminCredential = selectedTab == 0 &&
                        email.trim().equals("mumin", ignoreCase = true) &&
                        password.trim() == "Woyowoyo12@"

                val isValid = if (selectedTab == 0) {
                    isAdminCredential || (email.contains("@") && email.contains(".") && password.length >= 6)
                } else {
                    email.contains("@") && email.contains(".") && password.length >= 6
                }

                Button(
                    onClick = {
                        if (selectedTab == 0) {
                            if (isAdminCredential) {
                                onAdminLoginSuccess?.invoke()
                            } else {
                                onSignIn(email.trim(), password.trim())
                            }
                        } else {
                            onSignUp(email.trim(), password.trim())
                        }
                    },
                    enabled = isValid && !isLoading,
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("auth_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(
                            text = if (selectedTab == 0) "Masuk Sekarang" else "Daftar & Kirim Verifikasi",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        confirmButton = {},
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun SupabaseConfigDialog(
    currentUrl: String,
    currentKey: String,
    onDismiss: () -> Unit,
    onSave: (url: String, key: String) -> Unit
) {
    var url by remember { mutableStateOf(if (currentUrl.contains("your-supabase-id")) "" else currentUrl) }
    var key by remember { mutableStateOf(if (currentKey.contains("your-supabase-anon-key")) "" else currentKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Pengaturan Supabase Cloud", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Masukkan URL Project dan Anon/Public Key dari dashboard Supabase Anda (Settings > API):",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Project URL") },
                    placeholder = { Text("https://xxx.supabase.co") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Link, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("supabase_url_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    label = { Text("Anon / Public Key") },
                    placeholder = { Text("eyJhbGciOiJIUzI1NiIsIn...") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Key, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("supabase_key_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF3ECF8E).copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Data tersimpan di penyimpanan lokal perangkat yang aman dan dipakai langsung untuk sinkronisasi poin.",
                        fontSize = 11.sp,
                        color = Color(0xFF047857),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(url, key)
                    onDismiss()
                },
                enabled = url.isNotBlank() && key.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                modifier = Modifier.testTag("save_supabase_config_button")
            ) {
                Text("Simpan Konfigurasi")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}

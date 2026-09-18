package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark

@Composable
fun GameCuanHeader(
    points: Int,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onWalletClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 840.dp)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
            // App branding with the new Game & Coin Logo
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.game_cuan_logo_1789625822440),
                        contentDescription = "Logo Game Cuan",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                val isDark = MaterialTheme.colorScheme.background.red < 0.5f
                val gameBrush = if (isDark) {
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF60A5FA), // Biru Neon Terang
                            Color(0xFFA78BFA)  // Violet Neon Game
                        )
                    )
                } else {
                    Brush.linearGradient(
                        colors = listOf(
                            BrandPrimary,       // Biru Royal Modern
                            BrandSecondary      // Indigo Ungu
                        )
                    )
                }

                val cuanBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFD700), // Emas Kilau Amber
                        Color(0xFFFF9100), // Oranye Emas Cerah
                        GoldCoinDark       // Emas Mewah
                    )
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GAME",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        style = TextStyle(brush = gameBrush)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "CUAN",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        style = TextStyle(brush = cuanBrush)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = Color(0xFFFFD700).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PRO",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) Color(0xFFFFD700) else GoldCoinDark,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Right side: Theme Toggle Button + Coin Balance Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Theme Toggle Icon Button
                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .clickable { onToggleTheme() }
                        .testTag("theme_toggle_button"),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(
                        modifier = Modifier.size(38.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = isDarkTheme,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                            },
                            label = "theme_icon_transition"
                        ) { dark ->
                            if (dark) {
                                Icon(
                                    imageVector = Icons.Default.LightMode,
                                    contentDescription = "Beralih ke Tema Terang",
                                    tint = GoldCoin,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.DarkMode,
                                    contentDescription = "Beralih ke Tema Gelap",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Coin Balance Pill
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onWalletClicked() }
                        .testTag("top_bar_wallet_pill"),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Poin",
                            tint = GoldCoin,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "%,d".format(points),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
}

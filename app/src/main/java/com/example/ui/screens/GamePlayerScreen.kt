package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.GamePixItem
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark
import com.example.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GamePlayerScreen(
    game: GamePixItem,
    onClose: (durationSeconds: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var playSeconds by remember { mutableIntStateOf(0) }
    var isWebLoading by remember { mutableStateOf(true) }
    var earnedRewardBonus by remember { mutableStateOf(false) }

    // Live Play Timer ticker
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            playSeconds += 1
            if (playSeconds >= 60 && !earnedRewardBonus) {
                earnedRewardBonus = true
            }
        }
    }

    BackHandler {
        onClose(playSeconds)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("game_player_screen")
    ) {
        // Player Control Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1E293B),
            shadowElevation = 4.dp
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 840.dp)
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Close button
                        IconButton(
                            onClick = { onClose(playSeconds) },
                            modifier = Modifier.testTag("close_game_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup Game",
                                tint = Color.White
                            )
                        }

                        // Game Title & Category
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = game.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = GoldCoin,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                val minutes = playSeconds / 60
                                val seconds = playSeconds % 60
                                Text(
                                    text = String.format("%02d:%02d", minutes, seconds),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldCoin
                                )
                            }
                        }

                        // Reward badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (earnedRewardBonus) SuccessGreen.copy(alpha = 0.2f) else GoldCoin.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = if (earnedRewardBonus) SuccessGreen else GoldCoin,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (earnedRewardBonus) "+Bonus Aktif!" else "Cuan",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (earnedRewardBonus) SuccessGreen else GoldCoinDark
                                )
                            }
                        }
                    }

                    // Reward Progress Bar for 60s target
                    val progress = (playSeconds / 60f).coerceIn(0f, 1f)
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (earnedRewardBonus) SuccessGreen else GoldCoin,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                }
            }
        }

        // Web View Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val gameUrl = game.url ?: "https://play.gamepix.com/${game.namespace ?: "prism-match-3d"}/embed?sid=776E9"

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                            mediaPlaybackRequiresUserGesture = false
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isWebLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isWebLoading = false
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                return false
                            }
                        }
                        webChromeClient = WebChromeClient()
                        loadUrl(gameUrl)
                    }
                },
                update = { webView ->
                    // Keep view active
                },
                modifier = Modifier.fillMaxSize()
            )

            if (isWebLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.8f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = GoldCoin)
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Menghubungkan ke GamePix...",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Siap-siap kumpulkan koin cuan!",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

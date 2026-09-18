package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DanaKagetCampaign
import com.example.data.model.GamePixItem
import com.example.ui.components.DanaKagetLiveBanner
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.GoldCoin
import com.example.ui.theme.GoldCoinDark

@Composable
fun GamesScreen(
    games: List<GamePixItem>,
    isLoading: Boolean,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onGameClicked: (GamePixItem) -> Unit,
    onRefresh: () -> Unit,
    danaKagetCampaign: DanaKagetCampaign? = null,
    isDanaKagetClaimed: Boolean = false,
    onClaimDanaKaget: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Semua",
        "Match-3",
        "Farming",
        "Arcade",
        "Puzzle",
        "Racing",
        "Action",
        "Sports"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("games_screen_content")
    ) {
        // Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_game_field"),
                placeholder = {
                    Text(
                        "Cari game favoritmu...",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cari",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Hapus pencarian",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        }

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory.equals(category, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelected(category) },
                    label = {
                        Text(
                            text = category,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.testTag("category_chip_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Games Grid
        if (isLoading && games.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = BrandPrimary)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Memuat katalog GamePix...",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        } else if (games.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        "Tidak ada game yang cocok",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "Coba cari dengan kata kunci lain atau pilih kategori Semua",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRefresh,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Muat Ulang")
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .testTag("games_grid"),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Dana Kaget Drop Banner (Active & Flash Rush)
                if (danaKagetCampaign != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        DanaKagetLiveBanner(
                            campaign = danaKagetCampaign,
                            isClaimedByMe = isDanaKagetClaimed,
                            onClaimClick = onClaimDanaKaget,
                            onOpenAdminPanel = null
                        )
                    }
                }

                // Info banner item
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldCoin.copy(alpha = 0.12f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🪙", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Tips: Mainkan game selama minimal 1 menit untuk memenuhi misi harian dan klaim koin cuan!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = GoldCoinDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                items(games, key = { it.id }) { game ->
                    GameCard(
                        game = game,
                        onClick = { onGameClicked(game) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCard(
    game: GamePixItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("game_card_${game.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            // Image with Overlaid Quality and Play Icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.4f)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val imageUrl = game.bannerImage ?: game.image ?: ""
                AsyncImage(
                    model = imageUrl,
                    contentDescription = game.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Gradient overlay at bottom
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 100f
                            )
                        )
                )

                // Category pill on top-left
                game.category?.let { cat ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = cat.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Play icon overlay on center
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(alpha = 0.85f))
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Mainkan",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Quality rating pill on bottom-right
                val score = game.qualityScore?.let { (it * 10).toInt() / 10.0 } ?: 9.5
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = GoldCoin,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$score",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Title & Action
            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Text(
                    text = game.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bonus Cuan",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldCoin.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "+300 Poin",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldCoinDark,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

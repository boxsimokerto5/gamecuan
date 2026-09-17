package com.example.data.repository

import android.util.Log
import com.example.data.api.NetworkClient
import com.example.data.model.GamePixItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository {

    private val api = NetworkClient.apiService

    suspend fun getGames(page: Int = 1): Result<List<GamePixItem>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getGames(sid = "776E9", pagination = 24, page = page)
            if (response.items.isNotEmpty()) {
                Result.success(response.items)
            } else {
                Result.success(fallbackGames)
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Error fetching games from GamePix: ${e.message}", e)
            Result.success(fallbackGames)
        }
    }

    companion object {
        val fallbackGames = listOf(
            GamePixItem(
                id = "737HCH",
                title = "Prism Match 3D",
                namespace = "prism-match-3d",
                description = "Jelajahi teka-teki 3D memukau dengan kubus prisma warna-warni yang mengasah logika dan kecepatan tangan.",
                category = "match-3",
                orientation = "all",
                qualityScore = 0.98,
                bannerImage = "https://img.gamepix.com/games/prism-match-3d/cover/prism-match-3d.png?w=400",
                image = "https://img.gamepix.com/games/prism-match-3d/icon/prism-match-3d.png?w=160",
                url = "https://play.gamepix.com/prism-match-3d/embed?sid=776E9"
            ),
            GamePixItem(
                id = "5G91RE",
                title = "Garden Master",
                namespace = "garden-master",
                description = "Kembangkan kebun indah, rawat tanaman, dan panen benih emas untuk membuka berbagai bonus menguntungkan.",
                category = "farming",
                orientation = "portrait",
                qualityScore = 0.96,
                bannerImage = "https://img.gamepix.com/games/garden-master/cover/garden-master.png?w=400",
                image = "https://img.gamepix.com/games/garden-master/icon/garden-master.png?w=160",
                url = "https://play.gamepix.com/garden-master/embed?sid=776E9"
            ),
            GamePixItem(
                id = "920XAB",
                title = "Space Shooter Galaxy",
                namespace = "space-shooter-galaxy",
                description = "Kemudikan pesawat tempur luar angkasa, hancurkan armada musuh dan raih poin tertinggi di galaksi.",
                category = "arcade",
                orientation = "all",
                qualityScore = 0.95,
                bannerImage = "https://img.gamepix.com/games/space-shooter-galaxy/cover/space-shooter-galaxy.png?w=400",
                image = "https://img.gamepix.com/games/space-shooter-galaxy/icon/space-shooter-galaxy.png?w=160",
                url = "https://play.gamepix.com/space-shooter-galaxy/embed?sid=776E9"
            ),
            GamePixItem(
                id = "341PLQ",
                title = "Block Puzzle Classic",
                namespace = "block-puzzle-classic",
                description = "Susun balok-balok geometri legendaris, bersihkan garis dan kumpulkan poin kombo berturut-turut.",
                category = "puzzle",
                orientation = "portrait",
                qualityScore = 0.94,
                bannerImage = "https://img.gamepix.com/games/block-puzzle-classic/cover/block-puzzle-classic.png?w=400",
                image = "https://img.gamepix.com/games/block-puzzle-classic/icon/block-puzzle-classic.png?w=160",
                url = "https://play.gamepix.com/block-puzzle-classic/embed?sid=776E9"
            ),
            GamePixItem(
                id = "881NNV",
                title = "Turbo Moto Racer",
                namespace = "turbo-moto-racer",
                description = "Pacu adrenalin di jalan raya dengan motor kencang, hindari lalu lintas padat untuk meraih cuan besar.",
                category = "racing",
                orientation = "landscape",
                qualityScore = 0.97,
                bannerImage = "https://img.gamepix.com/games/turbo-moto-racer/cover/turbo-moto-racer.png?w=400",
                image = "https://img.gamepix.com/games/turbo-moto-racer/icon/turbo-moto-racer.png?w=160",
                url = "https://play.gamepix.com/turbo-moto-racer/embed?sid=776E9"
            ),
            GamePixItem(
                id = "619KMM",
                title = "Candy Sweet Rush",
                namespace = "candy-sweet-rush",
                description = "Cocokkan 3 permen manis lezat, picu ledakan permen warna-warni dan lewati ratusan stage menantang.",
                category = "match-3",
                orientation = "portrait",
                qualityScore = 0.93,
                bannerImage = "https://img.gamepix.com/games/candy-sweet-rush/cover/candy-sweet-rush.png?w=400",
                image = "https://img.gamepix.com/games/candy-sweet-rush/icon/candy-sweet-rush.png?w=160",
                url = "https://play.gamepix.com/candy-sweet-rush/embed?sid=776E9"
            )
        )
    }
}

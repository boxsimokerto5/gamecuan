package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GamePixFeedResponse(
    @Json(name = "version") val version: String? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "feed_url") val feedUrl: String? = null,
    @Json(name = "next_url") val nextUrl: String? = null,
    @Json(name = "items") val items: List<GamePixItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class GamePixItem(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "namespace") val namespace: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "orientation") val orientation: String? = null,
    @Json(name = "quality_score") val qualityScore: Double? = null,
    @Json(name = "banner_image") val bannerImage: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "url") val url: String? = null
)

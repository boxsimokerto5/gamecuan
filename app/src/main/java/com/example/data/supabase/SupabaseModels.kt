package com.example.data.supabase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseProfile(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String,
    @Json(name = "points") val points: Int = 500,
    @Json(name = "streak_days") val streakDays: Int = 1,
    @Json(name = "last_check_in_date") val lastCheckInDate: String = "",
    @Json(name = "games_played") val gamesPlayed: Int = 0,
    @Json(name = "minutes_played") val minutesPlayed: Int = 0,
    @Json(name = "updated_at") val updatedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseWithdrawal(
    @Json(name = "id") val id: String? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "wallet_provider") val walletProvider: String,
    @Json(name = "phone_number") val phoneNumber: String,
    @Json(name = "amount_rupiah") val amountRupiah: Int,
    @Json(name = "points_deducted") val pointsDeducted: Int,
    @Json(name = "status") val status: String = "BERHASIL",
    @Json(name = "reference_code") val referenceCode: String,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabasePointMutation(
    @Json(name = "id") val id: String? = null,
    @Json(name = "user_id") val userId: String,
    @Json(name = "title") val title: String,
    @Json(name = "points_change") val pointsChange: Int,
    @Json(name = "is_addition") val isAddition: Boolean,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthSignUpRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class AuthSignInRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "user") val user: AuthUser? = null,
    @Json(name = "msg") val msg: String? = null,
    @Json(name = "error_description") val errorDescription: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthUser(
    @Json(name = "id") val id: String,
    @Json(name = "email") val email: String?,
    @Json(name = "confirmed_at") val confirmedAt: String? = null,
    @Json(name = "email_confirmed_at") val emailConfirmedAt: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

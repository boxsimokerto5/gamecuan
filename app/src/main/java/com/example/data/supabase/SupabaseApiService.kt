package com.example.data.supabase

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApiService {

    // Supabase Auth Endpoints
    @Headers("Content-Type: application/json")
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body request: AuthSignUpRequest
    ): Response<AuthResponse>

    @Headers("Content-Type: application/json")
    @POST("auth/v1/token?grant_type=password")
    suspend fun signIn(
        @Header("apikey") apiKey: String,
        @Body request: AuthSignInRequest
    ): Response<AuthResponse>

    // Profiles table (user points & streak)
    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("rest/v1/profiles")
    suspend fun upsertProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Header("Prefer") prefer: String = "resolution=merge-duplicates",
        @Body profile: SupabaseProfile
    ): Response<List<SupabaseProfile>>

    @GET("rest/v1/profiles")
    suspend fun getProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("id") idFilter: String
    ): Response<List<SupabaseProfile>>

    // Withdrawals table
    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("rest/v1/withdrawals")
    suspend fun createWithdrawal(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Body withdrawal: SupabaseWithdrawal
    ): Response<List<SupabaseWithdrawal>>

    @GET("rest/v1/withdrawals")
    suspend fun getWithdrawals(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("user_id") userIdFilter: String,
        @Query("order") order: String = "created_at.desc"
    ): Response<List<SupabaseWithdrawal>>

    // Point mutations history
    @Headers("Content-Type: application/json", "Prefer: return=representation")
    @POST("rest/v1/point_mutations")
    suspend fun recordPointMutation(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Body mutation: SupabasePointMutation
    ): Response<List<SupabasePointMutation>>

    @GET("rest/v1/point_mutations")
    suspend fun getPointMutations(
        @Header("apikey") apiKey: String,
        @Header("Authorization") authorization: String,
        @Query("user_id") userIdFilter: String,
        @Query("order") order: String = "created_at.desc"
    ): Response<List<SupabasePointMutation>>
}

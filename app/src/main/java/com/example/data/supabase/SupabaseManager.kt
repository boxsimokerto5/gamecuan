package com.example.data.supabase

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class SupabaseManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("supabase_auth_prefs", Context.MODE_PRIVATE)

    // Default Supabase project credentials
    private val defaultUrl = "https://ejmwtpzdxsjdoylggxio.supabase.co/"
    private val defaultAnonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVqbXd0cHpkeHNqZG95bGdneGlvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODk2MjIzMDEsImV4cCI6MjEwNTE5ODMwMX0.a8Vgi3BS8X4YYuC28PvQIM217h1LMops4JzC39jF-T0"

    private val _projectUrl = MutableStateFlow(
        prefs.getString("supabase_url", null) ?: defaultUrl
    )
    val projectUrl: StateFlow<String> = _projectUrl.asStateFlow()

    private val _anonKey = MutableStateFlow(
        prefs.getString("supabase_anon_key", null) ?: defaultAnonKey
    )
    val anonKey: StateFlow<String> = _anonKey.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(
        prefs.getString("auth_user_id", null)
    )
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _currentUserEmail = MutableStateFlow<String?>(
        prefs.getString("auth_user_email", null)
    )
    val currentUserEmail: StateFlow<String?> = _currentUserEmail.asStateFlow()

    private val _accessToken = MutableStateFlow<String?>(
        prefs.getString("auth_access_token", null)
    )
    val accessToken: StateFlow<String?> = _accessToken.asStateFlow()

    private val _isEmailConfirmed = MutableStateFlow(
        prefs.getBoolean("auth_email_confirmed", false)
    )
    val isEmailConfirmed: StateFlow<Boolean> = _isEmailConfirmed.asStateFlow()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private var apiService: SupabaseApiService = createService(_projectUrl.value)

    private fun createService(baseUrl: String): SupabaseApiService {
        val cleanUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return try {
            Retrofit.Builder()
                .baseUrl(cleanUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SupabaseApiService::class.java)
        } catch (e: Exception) {
            Retrofit.Builder()
                .baseUrl("https://example.supabase.co/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(SupabaseApiService::class.java)
        }
    }

    fun isConfigured(): Boolean {
        val url = _projectUrl.value
        val key = _anonKey.value
        return url.isNotBlank() && !url.contains("your-supabase-id") &&
                key.isNotBlank() && !key.contains("your-supabase-anon-key")
    }

    fun saveConfiguration(url: String, key: String) {
        val trimmedUrl = url.trim().let { if (it.endsWith("/")) it else "$it/" }
        val trimmedKey = key.trim()
        _projectUrl.value = trimmedUrl
        _anonKey.value = trimmedKey
        prefs.edit()
            .putString("supabase_url", trimmedUrl)
            .putString("supabase_anon_key", trimmedKey)
            .apply()
        apiService = createService(trimmedUrl)
    }

    fun saveSession(user: AuthUser, token: String?) {
        _currentUserId.value = user.id
        _currentUserEmail.value = user.email
        _accessToken.value = token
        val confirmed = user.emailConfirmedAt != null || user.confirmedAt != null
        _isEmailConfirmed.value = confirmed

        prefs.edit()
            .putString("auth_user_id", user.id)
            .putString("auth_user_email", user.email)
            .putString("auth_access_token", token)
            .putBoolean("auth_email_confirmed", confirmed)
            .apply()
    }

    fun clearSession() {
        _currentUserId.value = null
        _currentUserEmail.value = null
        _accessToken.value = null
        _isEmailConfirmed.value = false

        prefs.edit()
            .remove("auth_user_id")
            .remove("auth_user_email")
            .remove("auth_access_token")
            .putBoolean("auth_email_confirmed", false)
            .apply()
    }

    suspend fun signUp(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.signUp(
                apiKey = _anonKey.value,
                request = AuthSignUpRequest(email.trim(), password)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.user?.let { user ->
                    saveSession(user, body.accessToken)
                }
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Gagal membuat akun"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = apiService.signIn(
                apiKey = _anonKey.value,
                request = AuthSignInRequest(email.trim(), password)
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                body.user?.let { user ->
                    saveSession(user, body.accessToken)
                }
                Result.success(body)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Email atau password salah"
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncProfile(profile: SupabaseProfile): Result<SupabaseProfile> {
        val token = _accessToken.value ?: return Result.failure(Exception("Belum login"))
        return try {
            val response = apiService.upsertProfile(
                apiKey = _anonKey.value,
                authorization = "Bearer $token",
                profile = profile
            )
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Gagal sinkron profil"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchProfile(): Result<SupabaseProfile?> {
        val userId = _currentUserId.value ?: return Result.failure(Exception("Belum login"))
        val token = _accessToken.value ?: return Result.failure(Exception("Token tidak valid"))
        return try {
            val response = apiService.getProfile(
                apiKey = _anonKey.value,
                authorization = "Bearer $token",
                idFilter = "eq.$userId"
            )
            if (response.isSuccessful) {
                Result.success(response.body()?.firstOrNull())
            } else {
                Result.failure(Exception(response.errorBody()?.string()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordWithdrawal(withdrawal: SupabaseWithdrawal): Result<SupabaseWithdrawal> {
        val token = _accessToken.value ?: return Result.failure(Exception("Belum login"))
        return try {
            val response = apiService.createWithdrawal(
                apiKey = _anonKey.value,
                authorization = "Bearer $token",
                withdrawal = withdrawal
            )
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Gagal mencatat penarikan"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun recordPointMutation(mutation: SupabasePointMutation): Result<SupabasePointMutation> {
        val token = _accessToken.value ?: return Result.failure(Exception("Belum login"))
        return try {
            val response = apiService.recordPointMutation(
                apiKey = _anonKey.value,
                authorization = "Bearer $token",
                mutation = mutation
            )
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                Result.success(response.body()!!.first())
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Gagal mencatat mutasi poin"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var instance: SupabaseManager? = null

        fun getInstance(context: Context): SupabaseManager {
            return instance ?: synchronized(this) {
                instance ?: SupabaseManager(context.applicationContext).also { instance = it }
            }
        }
    }
}

package com.example.myschoolhelper.data.remote

import com.example.myschoolhelper.config.AppConfig
import com.example.myschoolhelper.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Backend URL from configuration
    private val BASE_URL = AppConfig.BACKEND_BASE_URL

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private fun createClient(sessionManager: SessionManager? = null): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(AppConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(AppConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)

        sessionManager?.let {
            val authInterceptor = Interceptor { chain ->
                val token = it.getUserToken()
                val request = if (token != null) {
                    chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                } else {
                    chain.request()
                }
                chain.proceed(request)
            }
            builder.addInterceptor(authInterceptor)
        }

        return builder.build()
    }

    val authApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient())
            .build()
            .create(AuthApi::class.java)
    }

    fun createAuthenticatedApi(sessionManager: SessionManager): AttendanceApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient(sessionManager))
            .build()
            .create(AttendanceApi::class.java)
    }

    fun createAttendanceApi(): AttendanceApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient())
            .build()
            .create(AttendanceApi::class.java)
    }
}

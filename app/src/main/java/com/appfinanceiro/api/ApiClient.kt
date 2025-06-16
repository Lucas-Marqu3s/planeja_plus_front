package com.appfinanceiro.api

import com.appfinanceiro.util.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.GsonBuilder


class ApiClient(private val sessionManager: SessionManager) {
    
    private val BASE_URL = "http://3.88.162.0:8080/" // Endereço para acessar localhost do computador a partir do emulador
    
    private val authInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            
            // Não adicionar token para endpoints de autenticação
            if (originalRequest.url.encodedPath.contains("/auth/")) {
                return chain.proceed(originalRequest)
            }
            
            // Adicionar token JWT para outras requisições
            val token = sessionManager.fetchAuthToken()
            return if (token != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(originalRequest)
            }
        }
    }
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        .create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson)) // <-- use gson com formato customizado
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

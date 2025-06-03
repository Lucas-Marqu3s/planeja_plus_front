package com.appfinanceiro.api

import com.appfinanceiro.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    // Autenticação
    @POST("api/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>
    
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ResponseBody>
    
    // Usuários
    @GET("api/users/me")
    suspend fun getCurrentUser(): Response<User>
    
    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body userUpdateRequest: Any): Response<User>
    
    @PUT("api/users/{id}/password")
    suspend fun updatePassword(@Path("id") id: Long, @Body passwordUpdateRequest: Any): Response<User>
    
    // Transações
    @GET("api/transactions")
    suspend fun getAllTransactions(): Response<List<Transaction>>
    
    @GET("api/transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: Long): Response<Transaction>
    
    @POST("api/transactions")
    suspend fun createTransaction(@Body transactionRequest: TransactionRequest): Response<Transaction>
    
    @PUT("api/transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: Long, @Body transactionRequest: TransactionRequest): Response<Transaction>
    
    @DELETE("api/transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long): Response<Void>
    
    @GET("api/transactions/category/{category}")
    suspend fun getTransactionsByCategory(@Path("category") category: String): Response<List<Transaction>>
    
    @GET("api/transactions/type/{type}")
    suspend fun getTransactionsByType(@Path("type") type: TransactionType): Response<List<Transaction>>
    
    // Metas
    @GET("api/goals")
    suspend fun getAllGoals(): Response<List<Goal>>
    
    @GET("api/goals/{id}")
    suspend fun getGoalById(@Path("id") id: Long): Response<Goal>
    
    @POST("api/goals")
    suspend fun createGoal(@Body goalRequest: GoalRequest): Response<Goal>
    
    @PUT("api/goals/{id}")
    suspend fun updateGoal(@Path("id") id: Long, @Body goalRequest: GoalRequest): Response<Goal>
    
    @DELETE("api/goals/{id}")
    suspend fun deleteGoal(@Path("id") id: Long): Response<Void>
    
    @GET("api/goals/status/{status}")
    suspend fun getGoalsByStatus(@Path("status") status: GoalStatus): Response<List<Goal>>
}

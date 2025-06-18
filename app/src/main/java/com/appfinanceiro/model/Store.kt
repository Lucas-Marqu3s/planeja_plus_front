package com.appfinanceiro.model

data class Store(
    val id: Long,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val rating: Float,
    val website: String?,
    val category: String
)

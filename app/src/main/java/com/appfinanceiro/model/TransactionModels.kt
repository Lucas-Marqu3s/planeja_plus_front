package com.appfinanceiro.model

import java.math.BigDecimal
import java.util.Date

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: Long? = null,
    val type: TransactionType,
    val amount: BigDecimal,
    val date: Date,
    val description: String,
    val category: String
)

data class TransactionRequest(
    val type: TransactionType,
    val amount: BigDecimal,
    val date: Date,
    val description: String,
    val category: String
)

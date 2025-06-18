package com.appfinanceiro.model

import java.math.BigDecimal
import java.util.Date

// Mantenha apenas esta definição do enum
enum class GoalStatus {
    IN_PROGRESS, COMPLETED, CANCELED
}

data class Goal(
    val id: Long? = null,
    val name: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val deadline: Date? = null,
    val description: String? = null,
    val status: GoalStatus,
    val type: GoalType? = null
)
// Remova o enum vazio que estava aqui dentro

data class GoalRequest(
    val name: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val deadline: Date? = null,
    val description: String? = null,
    val status: GoalStatus,
    val type: GoalType? = null
)

enum class GoalType(val displayName: String) {
    CARRO("Carro"),
    CASA("Casa"),
    INVESTIMENTO("Investimento"),
    ROUPA("Roupa"),
    FERIAS("Férias"),
    COMPUTADOR("Computador")
}

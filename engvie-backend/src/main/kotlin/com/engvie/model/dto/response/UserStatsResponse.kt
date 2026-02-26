package com.engvie.model.dto.response

data class UserStatsResponse(
    val totalGames: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val winRate: Double,
    val avgAnswerTimeMs: Double?
)

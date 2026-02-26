package com.engvie.model.enum

enum class BotDifficulty(
    val correctAnswerRate: Double,
    val winCoins: Int,
    val drawCoins: Int,
    val lossCoins: Int
) {
    EASY(0.70, 5, 2, 1),
    MEDIUM(0.85, 12, 5, 2),
    HARD(0.95, 20, 8, 3)
}

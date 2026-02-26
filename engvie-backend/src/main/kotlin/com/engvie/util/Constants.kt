package com.engvie.util

object Constants {
    // Energy
    const val MAX_ENERGY = 5
    const val ENERGY_REGEN_MINUTES = 30L
    const val RANKED_GAME_ENERGY_COST = 1

    // Rating
    const val WIN_BASE_CHANGE = 25
    const val LOSS_BASE_CHANGE = -20
    const val DRAW_CHANGE = 5
    const val MIN_WIN_CHANGE = 15
    const val MAX_WIN_CHANGE = 35
    const val MIN_LOSS_CHANGE = -30
    const val MAX_LOSS_CHANGE = -10
    const val BOT_RATING_MULTIPLIER = 0.7

    // Matchmaking
    const val MATCHMAKING_RATING_RANGE = 200
    const val MATCHMAKING_TIMEOUT_SECONDS = 10L

    // Coins
    const val WIN_COINS = 10
    const val DRAW_COINS = 5
    const val LOSS_COINS = 2
    const val ENERGY_PURCHASE_COST = 60

    // Game
    const val QUESTION_TIMEOUT_MS = 16000
    const val ANSWER_DISPLAY_MS = 2000

    // Bot delays
    const val BOT_MIN_DELAY_MS = 3000L
    const val BOT_MAX_DELAY_MS = 8000L

    // Cache keys
    const val LEADERBOARD_CACHE_KEY = "leaderboard:global"
    const val LEADERBOARD_TTL_MINUTES = 10L
}

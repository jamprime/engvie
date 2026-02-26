package com.engvie.service

import com.engvie.model.enum.BotDifficulty
import com.engvie.model.enum.Rank
import org.springframework.stereotype.Service
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Service
class BotService {
    private val scheduler = Executors.newScheduledThreadPool(4)

    fun getBotDifficultyForRating(rating: Int): BotDifficulty {
        return when (Rank.fromRating(rating)) {
            Rank.BRONZE, Rank.SILVER -> BotDifficulty.EASY
            Rank.GOLD, Rank.PLATINUM -> BotDifficulty.MEDIUM
            else -> BotDifficulty.HARD
        }
    }

    fun getBotName(difficulty: BotDifficulty): String {
        return when (difficulty) {
            BotDifficulty.EASY -> "Bot_Novice"
            BotDifficulty.MEDIUM -> "Bot_Standard"
            BotDifficulty.HARD -> "Bot_Master"
        }
    }

    fun isCorrectAnswer(difficulty: BotDifficulty): Boolean {
        return Random.nextDouble() < difficulty.correctAnswerRate
    }

    fun scheduleAnswer(delayMs: Long, action: () -> Unit) {
        val delay = Random.nextLong(3000, 8000)
        scheduler.schedule(action, delay, TimeUnit.MILLISECONDS)
    }

    fun getBotRating(playerRating: Int): Int {
        return playerRating + Random.nextInt(-100, 101)
    }
}

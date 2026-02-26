package com.engvie.service

import com.engvie.model.entity.UserWord
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import kotlin.math.max

@Service
class SpacedRepetitionService {
    /**
     * SM-2 Algorithm implementation
     * quality: 0 = forgot, 3 = hard, 4 = good, 5 = easy
     */
    fun updateReview(userWord: UserWord, quality: Int): UserWord {
        val now = LocalDateTime.now()
        userWord.lastReviewed = now

        when {
            quality < 3 -> {
                // Failed - reset interval
                userWord.intervalDays = 1
                userWord.incorrectCount++
            }
            else -> {
                // Passed - update ease factor and interval
                userWord.easeFactor = max(
                    1.3,
                    userWord.easeFactor + 0.1 - (5 - quality) * (0.08 + (5 - quality) * 0.02)
                )
                userWord.intervalDays = when {
                    userWord.intervalDays == 1 -> 6
                    else -> (userWord.intervalDays * userWord.easeFactor).toInt()
                }
                if (quality == 5) {
                    userWord.intervalDays = (userWord.intervalDays * 1.3).toInt()
                }
                userWord.correctCount++
            }
        }

        userWord.nextReview = now.plusDays(userWord.intervalDays.toLong())
        userWord.status = when {
            userWord.correctCount >= 10 && userWord.easeFactor >= 2.5 -> "mastered"
            userWord.correctCount >= 3 -> "learned"
            else -> "learning"
        }
        userWord.updatedAt = now

        return userWord
    }
}

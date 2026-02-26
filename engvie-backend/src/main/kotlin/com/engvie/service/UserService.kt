package com.engvie.service

import com.engvie.model.dto.response.UserResponse
import com.engvie.model.dto.response.UserStatsResponse
import com.engvie.model.entity.User
import com.engvie.model.enum.Rank
import com.engvie.repository.GameRepository
import com.engvie.repository.UserRepository
import com.engvie.util.Constants
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class UserService(
    private val userRepository: UserRepository,
    private val gameRepository: GameRepository
) {
    fun getUserById(userId: Long): User {
        return userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
    }

    fun getUserResponse(userId: Long): UserResponse {
        return UserResponse.from(getUserById(userId))
    }

    fun getStats(userId: Long): UserStatsResponse {
        val user = getUserById(userId)
        val winRate = if (user.totalGames > 0) user.wins.toDouble() / user.totalGames else 0.0
        val avgTime = gameRepository.avgAnswerTimeByUserId(userId)
        return UserStatsResponse(
            totalGames = user.totalGames,
            wins = user.wins,
            losses = user.losses,
            draws = user.draws,
            winRate = winRate,
            avgAnswerTimeMs = avgTime
        )
    }

    @Transactional
    fun handleDailyLogin(user: User) {
        val today = LocalDate.now()
        val lastLogin = user.lastLoginDate

        when {
            lastLogin == null -> {
                user.streakDays = 1
                awardLoginCoins(user)
            }
            lastLogin == today -> {
                // Already logged in today, do nothing
            }
            ChronoUnit.DAYS.between(lastLogin, today) == 1L -> {
                user.streakDays++
                awardLoginCoins(user)
            }
            else -> {
                user.streakDays = 1
                awardLoginCoins(user)
            }
        }
        user.lastLoginDate = today
    }

    private fun awardLoginCoins(user: User) {
        val coins = when {
            user.streakDays % 7 == 0 -> {
                user.energy = minOf(user.energy + 1, user.maxEnergy)
                50
            }
            else -> minOf(user.streakDays * 5, 30)
        }
        user.coins += coins
    }

    @Transactional
    fun updateRating(user: User, ratingChange: Int) {
        user.rating = maxOf(0, user.rating + ratingChange)
        user.rank = Rank.fromRating(user.rating).name
        user.updatedAt = LocalDateTime.now()
        userRepository.save(user)
    }

    @Transactional
    fun save(user: User): User {
        user.updatedAt = LocalDateTime.now()
        return userRepository.save(user)
    }
}

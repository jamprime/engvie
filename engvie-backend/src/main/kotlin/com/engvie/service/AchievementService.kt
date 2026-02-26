package com.engvie.service

import com.engvie.model.dto.response.AchievementResponse
import com.engvie.model.dto.response.AchievementsListResponse
import com.engvie.model.entity.UserAchievement
import com.engvie.repository.AchievementRepository
import com.engvie.repository.UserAchievementRepository
import com.engvie.repository.UserWordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AchievementService(
    private val achievementRepository: AchievementRepository,
    private val userAchievementRepository: UserAchievementRepository,
    private val userWordRepository: UserWordRepository
) {
    fun getAchievements(userId: Long): AchievementsListResponse {
        val allAchievements = achievementRepository.findAll()
        val userAchievements = userAchievementRepository.findByUserId(userId)
            .associateBy { it.achievement.id }

        val responses = allAchievements.map { achievement ->
            val ua = userAchievements[achievement.id]
            AchievementResponse.from(achievement, ua != null, ua?.unlockedAt)
        }

        return AchievementsListResponse(
            achievements = responses,
            totalUnlocked = userAchievements.size
        )
    }

    @Transactional
    fun checkAndUnlockAchievements(userId: Long, userService: UserService): List<AchievementResponse> {
        val user = userService.getUserById(userId)
        val allAchievements = achievementRepository.findAll()
        val unlockedIds = userAchievementRepository.findByUserId(userId)
            .map { it.achievement.id }.toSet()

        val newlyUnlocked = mutableListOf<AchievementResponse>()

        allAchievements.filter { it.id !in unlockedIds }.forEach { achievement ->
            val met = when (achievement.conditionType) {
                "wins_count" -> user.wins >= achievement.conditionValue
                "streak_days" -> user.streakDays >= achievement.conditionValue
                "words_learned" -> userWordRepository.countByUserId(userId) >= achievement.conditionValue
                "total_games" -> user.totalGames >= achievement.conditionValue
                else -> false
            }

            if (met) {
                userAchievementRepository.save(
                    UserAchievement(user = user, achievement = achievement)
                )
                user.coins += achievement.rewardCoins
                userService.save(user)
                newlyUnlocked.add(AchievementResponse.from(achievement, true, LocalDateTime.now()))
            }
        }

        return newlyUnlocked
    }
}

package com.engvie.model.dto.response

import com.engvie.model.entity.Achievement
import java.time.LocalDateTime

data class AchievementResponse(
    val id: Int,
    val code: String,
    val nameEn: String,
    val nameRu: String,
    val descriptionRu: String?,
    val icon: String,
    val rewardCoins: Int,
    val conditionType: String,
    val conditionValue: Int,
    val unlocked: Boolean,
    val unlockedAt: LocalDateTime?
) {
    companion object {
        fun from(achievement: Achievement, unlocked: Boolean, unlockedAt: LocalDateTime?) = AchievementResponse(
            id = achievement.id,
            code = achievement.code,
            nameEn = achievement.nameEn,
            nameRu = achievement.nameRu,
            descriptionRu = achievement.descriptionRu,
            icon = achievement.icon,
            rewardCoins = achievement.rewardCoins,
            conditionType = achievement.conditionType,
            conditionValue = achievement.conditionValue,
            unlocked = unlocked,
            unlockedAt = unlockedAt
        )
    }
}

data class AchievementsListResponse(
    val achievements: List<AchievementResponse>,
    val totalUnlocked: Int
)

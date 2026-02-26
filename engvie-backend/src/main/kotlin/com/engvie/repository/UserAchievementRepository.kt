package com.engvie.repository

import com.engvie.model.entity.UserAchievement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAchievementRepository : JpaRepository<UserAchievement, Int> {
    fun findByUserId(userId: Long): List<UserAchievement>
    fun existsByUserIdAndAchievementId(userId: Long, achievementId: Int): Boolean
}

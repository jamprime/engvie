package com.engvie.repository

import com.engvie.model.entity.Achievement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AchievementRepository : JpaRepository<Achievement, Int> {
    fun findByCode(code: String): Achievement?
}

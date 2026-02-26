package com.engvie.controller

import com.engvie.model.dto.response.AchievementsListResponse
import com.engvie.service.AchievementService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/achievements")
class AchievementController(
    private val achievementService: AchievementService
) {
    @GetMapping
    fun getAchievements(auth: Authentication): ResponseEntity<AchievementsListResponse> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(achievementService.getAchievements(userId))
    }
}

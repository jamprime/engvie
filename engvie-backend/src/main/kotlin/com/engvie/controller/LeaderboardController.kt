package com.engvie.controller

import com.engvie.model.dto.response.LeaderboardResponse
import com.engvie.service.LeaderboardService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/leaderboard")
class LeaderboardController(private val leaderboardService: LeaderboardService) {
    @GetMapping("/global")
    fun getGlobalLeaderboard(auth: Authentication): ResponseEntity<LeaderboardResponse> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(leaderboardService.getLeaderboardForUser(userId))
    }
}

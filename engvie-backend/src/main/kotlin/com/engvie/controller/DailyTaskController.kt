package com.engvie.controller

import com.engvie.service.DailyTaskService
import com.engvie.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/daily-tasks")
class DailyTaskController(
    private val dailyTaskService: DailyTaskService,
    private val userService: UserService
) {
    @GetMapping
    fun getTasks(auth: Authentication): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val tasks = dailyTaskService.getTodaysTasks(userId)
        return ResponseEntity.ok(mapOf("tasks" to tasks))
    }

    @PostMapping("/{taskId}/claim")
    fun claimReward(
        auth: Authentication,
        @PathVariable taskId: Int
    ): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val coins = dailyTaskService.claimReward(userId, taskId, userService)
        return ResponseEntity.ok(mapOf("coins" to coins))
    }
}

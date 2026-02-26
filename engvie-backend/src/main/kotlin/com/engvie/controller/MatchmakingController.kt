package com.engvie.controller

import com.engvie.model.dto.request.MatchmakingJoinRequest
import com.engvie.service.EnergyService
import com.engvie.service.MatchmakingService
import com.engvie.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/matchmaking")
class MatchmakingController(
    private val matchmakingService: MatchmakingService,
    private val userService: UserService,
    private val energyService: EnergyService
) {
    @PostMapping("/join")
    fun joinQueue(
        auth: Authentication,
        @RequestBody request: MatchmakingJoinRequest
    ): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val user = userService.getUserById(userId)

        // Check and consume energy
        if (!energyService.consumeEnergy(user, 1)) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Not enough energy", "timeToNextEnergy" to energyService.getTimeToNextEnergy(user)))
        }

        val queue = matchmakingService.joinQueue(user, request.roundsCount)
        return ResponseEntity.ok(mapOf("queueId" to queue.id))
    }

    @DeleteMapping("/cancel")
    fun cancelQueue(auth: Authentication): ResponseEntity<Map<String, Boolean>> {
        val userId = auth.principal as Long
        matchmakingService.cancelQueue(userId)
        return ResponseEntity.ok(mapOf("success" to true))
    }
}

package com.engvie.controller

import com.engvie.model.dto.response.UserResponse
import com.engvie.model.dto.response.UserStatsResponse
import com.engvie.service.EnergyService
import com.engvie.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
    private val energyService: EnergyService
) {
    @GetMapping("/me")
    fun getMe(auth: Authentication): ResponseEntity<UserResponse> {
        val userId = auth.principal as Long
        val user = userService.getUserById(userId)
        energyService.recoverEnergy(user)
        return ResponseEntity.ok(UserResponse.from(user))
    }

    @GetMapping("/me/stats")
    fun getStats(auth: Authentication): ResponseEntity<UserStatsResponse> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(userService.getStats(userId))
    }

    @PostMapping("/me/buy-energy")
    fun buyEnergy(auth: Authentication): ResponseEntity<Any> {
        val userId = auth.principal as Long
        return try {
            val user = userService.getUserById(userId)
            val updated = energyService.buyEnergy(user)
            ResponseEntity.ok(UserResponse.from(updated))
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}

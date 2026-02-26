package com.engvie.controller

import com.engvie.model.dto.request.AuthRequest
import com.engvie.model.dto.response.AuthResponse
import com.engvie.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {
    @PostMapping("/telegram")
    fun authenticate(@Valid @RequestBody request: AuthRequest): ResponseEntity<AuthResponse> {
        return try {
            ResponseEntity.ok(authService.authenticate(request))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}

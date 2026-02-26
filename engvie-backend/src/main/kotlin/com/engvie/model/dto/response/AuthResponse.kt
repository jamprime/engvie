package com.engvie.model.dto.response

data class AuthResponse(
    val token: String,
    val user: UserResponse
)

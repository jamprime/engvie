package com.engvie.model.dto.request

import jakarta.validation.constraints.NotBlank

data class AuthRequest(
    @field:NotBlank
    val initData: String
)

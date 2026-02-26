package com.engvie.model.dto.request

import jakarta.validation.constraints.Pattern

data class MatchmakingJoinRequest(
    @field:Pattern(regexp = "3|5", message = "roundsCount must be 3 or 5")
    val roundsCount: Int = 3
)

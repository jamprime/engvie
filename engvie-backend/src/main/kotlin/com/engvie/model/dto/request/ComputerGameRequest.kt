package com.engvie.model.dto.request

data class ComputerGameRequest(
    val difficulty: String = "medium",
    val roundsCount: Int = 3
)

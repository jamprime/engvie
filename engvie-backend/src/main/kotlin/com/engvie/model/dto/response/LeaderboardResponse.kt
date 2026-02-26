package com.engvie.model.dto.response

data class LeaderboardEntry(
    val rank: Int,
    val userId: Long,
    val username: String,
    val rating: Int,
    val rankTitle: String,
    val wins: Int
)

data class LeaderboardResponse(
    val leaderboard: List<LeaderboardEntry>,
    val myPosition: Int
)

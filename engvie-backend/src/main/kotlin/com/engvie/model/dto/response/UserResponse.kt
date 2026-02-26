package com.engvie.model.dto.response

import com.engvie.model.entity.User
import com.engvie.util.Constants
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class UserResponse(
    val id: Long,
    val username: String,
    val firstName: String,
    val lastName: String?,
    val rating: Int,
    val rank: String,
    val level: String,
    val totalGames: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val energy: Int,
    val maxEnergy: Int,
    val nextEnergyInSeconds: Long?,   // null when energy is full
    val coins: Int,
    val streakDays: Int
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id,
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            rating = user.rating,
            rank = user.rank,
            level = user.level,
            totalGames = user.totalGames,
            wins = user.wins,
            losses = user.losses,
            draws = user.draws,
            energy = user.energy,
            maxEnergy = user.maxEnergy,
            nextEnergyInSeconds = if (user.energy < user.maxEnergy) {
                val nextTime = user.lastEnergyUpdate.plusMinutes(Constants.ENERGY_REGEN_MINUTES)
                ChronoUnit.SECONDS.between(LocalDateTime.now(), nextTime).coerceAtLeast(0)
            } else null,
            coins = user.coins,
            streakDays = user.streakDays
        )
    }
}

package com.engvie.service

import com.engvie.model.entity.User
import com.engvie.repository.UserRepository
import com.engvie.util.Constants
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class EnergyService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun recoverEnergy(user: User) {
        val now = LocalDateTime.now()
        val minutesPassed = ChronoUnit.MINUTES.between(user.lastEnergyUpdate, now)
        val energyToAdd = (minutesPassed / Constants.ENERGY_REGEN_MINUTES).toInt()

        if (energyToAdd > 0 && user.energy < user.maxEnergy) {
            user.energy = minOf(user.energy + energyToAdd, user.maxEnergy)
            user.lastEnergyUpdate = user.lastEnergyUpdate.plusMinutes(
                energyToAdd * Constants.ENERGY_REGEN_MINUTES
            )
            userRepository.save(user)
        }
    }

    @Transactional
    fun consumeEnergy(user: User, amount: Int = 1): Boolean {
        recoverEnergy(user)
        return if (user.energy >= amount) {
            user.energy -= amount
            userRepository.save(user)
            true
        } else {
            false
        }
    }

    @Transactional
    fun buyEnergy(user: User): User {
        recoverEnergy(user)
        if (user.energy >= user.maxEnergy) throw IllegalStateException("Energy is already full")
        if (user.coins < Constants.ENERGY_PURCHASE_COST) throw IllegalStateException("Not enough coins")
        user.coins -= Constants.ENERGY_PURCHASE_COST
        user.energy += 1
        return userRepository.save(user)
    }

    fun getTimeToNextEnergy(user: User): Long {
        if (user.energy >= user.maxEnergy) return 0
        val nextEnergyTime = user.lastEnergyUpdate.plusMinutes(Constants.ENERGY_REGEN_MINUTES)
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), nextEnergyTime).coerceAtLeast(0)
    }
}

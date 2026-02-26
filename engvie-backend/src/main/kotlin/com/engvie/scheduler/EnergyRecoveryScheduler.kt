package com.engvie.scheduler

import com.engvie.repository.UserRepository
import com.engvie.service.TelegramBotService
import com.engvie.util.Constants
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Component
class EnergyRecoveryScheduler(
    private val userRepository: UserRepository,
    private val telegramBotService: TelegramBotService
) {
    private val log = LoggerFactory.getLogger(EnergyRecoveryScheduler::class.java)

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    fun recoverEnergy() {
        val now = LocalDateTime.now()
        val threshold = now.minusMinutes(Constants.ENERGY_REGEN_MINUTES)
        val users = userRepository.findUsersNeedingEnergyRecovery(threshold)

        for (user in users) {
            val minutesPassed = ChronoUnit.MINUTES.between(user.lastEnergyUpdate, now)
            val energyToAdd = (minutesPassed / Constants.ENERGY_REGEN_MINUTES).toInt()
            if (energyToAdd <= 0) continue

            val energyBefore = user.energy
            user.energy = minOf(user.energy + energyToAdd, user.maxEnergy)
            user.lastEnergyUpdate = user.lastEnergyUpdate.plusMinutes(energyToAdd * Constants.ENERGY_REGEN_MINUTES)
            userRepository.save(user)

            log.debug("Energy recovered: userId={} {}→{}", user.id, energyBefore, user.energy)
            telegramBotService.sendEnergyNotification(user.id, user.energy, user.maxEnergy)
        }
    }
}

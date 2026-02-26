package com.engvie.scheduler

import com.engvie.repository.MatchmakingQueueRepository
import com.engvie.service.GameService
import com.engvie.util.Constants
import org.slf4j.LoggerFactory
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class MatchmakingScheduler(
    private val matchmakingQueueRepository: MatchmakingQueueRepository,
    private val gameService: GameService,
    private val messagingTemplate: SimpMessagingTemplate
) {
    private val logger = LoggerFactory.getLogger(MatchmakingScheduler::class.java)

    @Scheduled(fixedDelay = 2000) // Every 2 seconds
    @Transactional
    fun processQueue() {
        val searching = matchmakingQueueRepository.findAllByStatus("searching")
        if (searching.isEmpty()) return

        val timedOut = matchmakingQueueRepository.findTimedOutEntries(
            LocalDateTime.now().minusSeconds(Constants.MATCHMAKING_TIMEOUT_SECONDS)
        )

        val matched = mutableSetOf<Int>()

        // Match timed-out players with bots
        timedOut.forEach { entry ->
            if (entry.id in matched) return@forEach
            matched.add(entry.id)

            try {
                val game = gameService.createComputerGame(
                    player1 = entry.user,
                    difficulty = "medium",
                    roundsCount = entry.roundsPreference
                )
                entry.status = "matched"
                matchmakingQueueRepository.save(entry)

                messagingTemplate.convertAndSendToUser(
                    entry.user.id.toString(),
                    "/queue/matchmaking",
                    mapOf("type" to "MATCHMAKING_FOUND", "gameId" to game.id, "opponent" to "bot")
                )
                logger.info("Matched user ${entry.user.id} with bot -> game ${game.id}")
            } catch (e: Exception) {
                logger.error("Failed to create bot game for user ${entry.user.id}", e)
            }
        }

        // Try to match remaining players with each other
        val unmatched = searching.filter { it.id !in matched && it.id !in timedOut.map { t -> t.id }.toSet() }

        val processed = mutableSetOf<Int>()
        unmatched.forEach { entry ->
            if (entry.id in processed) return@forEach

            val opponent = matchmakingQueueRepository.findOpponent(
                excludeUserId = entry.user.id,
                minRating = entry.rating - Constants.MATCHMAKING_RATING_RANGE,
                maxRating = entry.rating + Constants.MATCHMAKING_RATING_RANGE,
                targetRating = entry.rating
            ) ?: return@forEach

            if (opponent.id in processed) return@forEach

            processed.add(entry.id)
            processed.add(opponent.id)

            try {
                val game = gameService.createRankedGame(
                    player1 = entry.user,
                    player2 = opponent.user,
                    roundsCount = minOf(entry.roundsPreference, opponent.roundsPreference)
                )

                entry.status = "matched"
                opponent.status = "matched"
                matchmakingQueueRepository.save(entry)
                matchmakingQueueRepository.save(opponent)

                val payload = mapOf("type" to "MATCHMAKING_FOUND", "gameId" to game.id)
                messagingTemplate.convertAndSendToUser(entry.user.id.toString(), "/queue/matchmaking", payload)
                messagingTemplate.convertAndSendToUser(opponent.user.id.toString(), "/queue/matchmaking", payload)

                logger.info("Matched users ${entry.user.id} and ${opponent.user.id} -> game ${game.id}")
            } catch (e: Exception) {
                logger.error("Failed to create ranked game", e)
            }
        }
    }
}

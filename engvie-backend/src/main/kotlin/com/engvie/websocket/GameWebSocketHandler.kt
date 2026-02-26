package com.engvie.websocket

import com.engvie.service.GameService
import com.engvie.service.GameFinishResult
import com.engvie.service.RoundStartData
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import java.security.Principal
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Controller
class GameWebSocketHandler(
    private val messagingTemplate: SimpMessagingTemplate,
    private val gameService: GameService,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(GameWebSocketHandler::class.java)

    @MessageMapping("/game.join")
    fun joinGame(@Payload message: Map<String, Any>, principal: Principal) {
        val gameId = (message["gameId"] as? Number)?.toInt() ?: return
        val userId = principal.name.toLongOrNull() ?: return

        log.info("game.join: gameId={}, userId={}", gameId, userId)
        try {
            val gameInfo = gameService.getGameInfo(gameId) ?: return

            when {
                gameInfo.currentTurnId == userId -> {
                    val roundData = gameService.getNextRoundStartData(gameId, userId)
                    if (roundData != null) {
                        log.info("game.join: sending ROUND_START roundId={} to userId={}", roundData.roundId, userId)
                        sendRoundStart(principal.name, roundData)
                    }
                }
                gameInfo.isBotGame && gameInfo.currentTurnId == null -> {
                    // Bot is thinking — do nothing, executor will fire
                    log.info("game.join: bot is thinking, userId={} waits", userId)
                }
                else -> {
                    // Opponent's turn — tell this player to wait and show opponent's current question
                    val opponentId = gameInfo.currentTurnId ?: return
                    val opponentRound = gameService.getNextRoundStartData(gameId, opponentId)
                    val questionMap = if (opponentRound != null) {
                        @Suppress("UNCHECKED_CAST")
                        val options = objectMapper.readValue(opponentRound.options, List::class.java) as List<String>
                        mapOf(
                            "word" to opponentRound.word,
                            "direction" to opponentRound.direction,
                            "options" to options
                        )
                    } else null

                    messagingTemplate.convertAndSendToUser(
                        principal.name, "/queue/game",
                        mapOf("type" to "OPPONENT_TURN", "playerId" to opponentId, "question" to questionMap)
                    )
                }
            }
        } catch (e: Exception) {
            log.error("game.join: exception for gameId={}, userId={}", gameId, userId, e)
            messagingTemplate.convertAndSendToUser(
                principal.name, "/queue/game",
                mapOf("type" to "ERROR", "message" to "Failed to join game")
            )
        }
    }

    @MessageMapping("/game.answer")
    fun submitAnswer(@Payload message: Map<String, Any>, principal: Principal) {
        val gameId = (message["gameId"] as? Number)?.toInt() ?: return
        val roundId = (message["roundId"] as? Number)?.toInt() ?: return
        val answer = message["answer"] as? String ?: ""
        val answerTimeMs = (message["answerTimeMs"] as? Number)?.toInt() ?: 0
        val userId = principal.name.toLongOrNull() ?: return

        try {
            val result = gameService.submitAnswer(gameId, roundId, userId, answer, answerTimeMs)

            // Send answer result to the answering player
            messagingTemplate.convertAndSendToUser(
                principal.name, "/queue/game",
                mapOf(
                    "type" to "ANSWER_RESULT",
                    "roundId" to roundId,
                    "isCorrect" to result.isCorrect,
                    "correctAnswer" to result.correctAnswer,
                    "currentScore" to mapOf(
                        "player1" to result.currentScore.first,
                        "player2" to result.currentScore.second
                    )
                )
            )

            if (result.gameFinished != null) {
                sendGameFinished(gameId, result.gameFinished, result.currentScore)
                return
            }

            val gameInfo = gameService.getGameInfo(gameId) ?: return

            if (gameInfo.isBotGame) {
                // Generate bot's question for display, then think for 2s
                val botQuestion = gameService.generateBotQuestion(gameId)

                // Immediately show player what question the bot is answering
                messagingTemplate.convertAndSendToUser(
                    principal.name, "/queue/game",
                    mapOf(
                        "type" to "OPPONENT_TURN",
                        "playerId" to 0,
                        "question" to mapOf(
                            "word" to botQuestion.word,
                            "direction" to botQuestion.direction,
                            "options" to botQuestion.options
                        )
                    )
                )

                // After 2s: bot "answers"
                CompletableFuture.delayedExecutor(2000L, TimeUnit.MILLISECONDS).execute {
                    try {
                        val botResult = gameService.simulateBotTurn(gameId)

                        // Compute which option bot selected
                        val botSelectedAnswer = if (botResult.isCorrect) {
                            botQuestion.correctAnswer
                        } else {
                            botQuestion.options.filter { it != botQuestion.correctAnswer }.randomOrNull()
                                ?: botQuestion.correctAnswer
                        }

                        messagingTemplate.convertAndSendToUser(
                            principal.name, "/queue/game",
                            mapOf(
                                "type" to "OPPONENT_ANSWERED",
                                "playerId" to 0,
                                "isCorrect" to botResult.isCorrect,
                                "selectedAnswer" to botSelectedAnswer,
                                "correctAnswer" to botQuestion.correctAnswer,
                                "currentScore" to mapOf(
                                    "player1" to botResult.currentScore.first,
                                    "player2" to botResult.currentScore.second
                                )
                            )
                        )

                        if (botResult.gameFinished != null) {
                            sendGameFinished(gameId, botResult.gameFinished, botResult.currentScore)
                        } else {
                            // After brief pause so player sees bot's result
                            CompletableFuture.delayedExecutor(800L, TimeUnit.MILLISECONDS).execute {
                                val nextRound = gameService.getNextRoundStartData(gameId, userId)
                                if (nextRound != null) sendRoundStart(principal.name, nextRound)
                            }
                        }
                    } catch (e: Exception) {
                        log.error("Bot turn failed for gameId={}", gameId, e)
                    }
                }
            } else {
                // PvP: switch to next player
                val nextPlayerId = result.nextTurnPlayerId ?: return
                val nextPlayerStr = nextPlayerId.toString()

                // Get next player's round (to show to the waiting player)
                val nextRound = gameService.getNextRoundStartData(gameId, nextPlayerId)

                // Tell P2 (waiting) about P1's result
                messagingTemplate.convertAndSendToUser(
                    nextPlayerStr, "/queue/game",
                    mapOf(
                        "type" to "OPPONENT_ANSWERED",
                        "playerId" to userId,
                        "isCorrect" to result.isCorrect,
                        "selectedAnswer" to answer,
                        "correctAnswer" to result.correctAnswer,
                        "currentScore" to mapOf(
                            "player1" to result.currentScore.first,
                            "player2" to result.currentScore.second
                        )
                    )
                )

                // Immediately show P1 what P2 will be answering
                val questionMap = if (nextRound != null) {
                    @Suppress("UNCHECKED_CAST")
                    val options = objectMapper.readValue(nextRound.options, List::class.java) as List<String>
                    mapOf("word" to nextRound.word, "direction" to nextRound.direction, "options" to options)
                } else null
                messagingTemplate.convertAndSendToUser(
                    principal.name, "/queue/game",
                    mapOf("type" to "OPPONENT_TURN", "playerId" to nextPlayerId, "question" to questionMap)
                )

                // After 800ms: P2 starts answering (they've seen P1's result)
                CompletableFuture.delayedExecutor(800L, TimeUnit.MILLISECONDS).execute {
                    if (nextRound != null) {
                        sendRoundStart(nextPlayerStr, nextRound)
                    }
                }
            }
        } catch (e: Exception) {
            messagingTemplate.convertAndSendToUser(
                principal.name, "/queue/game",
                mapOf("type" to "ERROR", "message" to (e.message ?: "Unknown error"))
            )
        }
    }

    private fun sendRoundStart(principalName: String, roundData: RoundStartData) {
        @Suppress("UNCHECKED_CAST")
        val options = objectMapper.readValue(roundData.options, List::class.java) as List<String>
        messagingTemplate.convertAndSendToUser(
            principalName, "/queue/game",
            mapOf(
                "type" to "ROUND_START",
                "roundId" to roundData.roundId,
                "playerId" to roundData.playerId,
                "question" to mapOf(
                    "word" to roundData.word,
                    "direction" to roundData.direction,
                    "options" to options
                ),
                "timeLimit" to roundData.timeLimit
            )
        )
    }

    private fun sendGameFinished(gameId: Int, result: GameFinishResult, score: Pair<Int, Int>) {
        messagingTemplate.convertAndSend(
            "/topic/game/$gameId",
            mapOf(
                "type" to "GAME_FINISHED",
                "gameId" to gameId,
                "winnerId" to result.winnerId,
                "player1Id" to result.player1Id,
                "player1RatingChange" to result.player1RatingChange,
                "player2RatingChange" to result.player2RatingChange,
                "coinsEarned" to result.coinsEarned,
                "finalScore" to mapOf("player1" to score.first, "player2" to score.second)
            )
        )
    }
}

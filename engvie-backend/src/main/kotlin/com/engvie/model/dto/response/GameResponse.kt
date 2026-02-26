package com.engvie.model.dto.response

import com.engvie.model.entity.Game
import java.time.LocalDateTime

data class GameResponse(
    val id: Int,
    val player1: PlayerInfo,
    val player2: PlayerInfo?,
    val player1Score: Int,
    val player2Score: Int,
    val winnerId: Long?,
    val gameType: String,
    val roundsCount: Int,
    val status: String,
    val currentTurn: Long?,
    val isBotGame: Boolean,
    val botDifficulty: String?,
    val startedAt: LocalDateTime?,
    val finishedAt: LocalDateTime?,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(game: Game) = GameResponse(
            id = game.id,
            player1 = PlayerInfo(game.player1.id, game.player1.username, game.player1.rating, game.player1.rank),
            player2 = game.player2?.let { PlayerInfo(it.id, it.username, it.rating, it.rank) },
            player1Score = game.player1Score,
            player2Score = game.player2Score,
            winnerId = game.winnerId,
            gameType = game.gameType,
            roundsCount = game.roundsCount,
            status = game.status,
            currentTurn = game.currentTurn,
            isBotGame = game.isBotGame,
            botDifficulty = game.botDifficulty,
            startedAt = game.startedAt,
            finishedAt = game.finishedAt,
            createdAt = game.createdAt
        )
    }
}

data class PlayerInfo(
    val id: Long,
    val username: String,
    val rating: Int,
    val rank: String
)

package com.engvie.repository

import com.engvie.model.entity.GameRound
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameRoundRepository : JpaRepository<GameRound, Int> {
    fun findByGameIdOrderByRoundNumber(gameId: Int): List<GameRound>

    fun findByGameIdAndPlayerId(gameId: Int, playerId: Long): List<GameRound>

    @Query("SELECT gr FROM GameRound gr WHERE gr.game.id = :gameId AND gr.player.id = :playerId AND gr.userAnswer IS NULL ORDER BY gr.roundNumber")
    fun findNextRound(gameId: Int, playerId: Long): List<GameRound>

    @Query("SELECT COUNT(gr) FROM GameRound gr WHERE gr.game.id = :gameId AND gr.player.id = :playerId AND gr.userAnswer IS NOT NULL")
    fun countAnsweredRounds(gameId: Int, playerId: Long): Long

    @Query("SELECT COUNT(gr) FROM GameRound gr WHERE gr.player.id = :userId AND gr.isCorrect = true")
    fun countCorrectAnswersByUserId(userId: Long): Long
}

package com.engvie.repository

import com.engvie.model.entity.Game
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface GameRepository : JpaRepository<Game, Int> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Game g WHERE g.id = :id")
    fun findByIdForUpdate(id: Int): Game?


    @Query("SELECT g FROM Game g WHERE (g.player1.id = :userId OR g.player2.id = :userId) ORDER BY g.createdAt DESC")
    fun findByUserId(userId: Long, pageable: Pageable): List<Game>

    @Query("SELECT COUNT(g) FROM Game g WHERE (g.player1.id = :userId OR g.player2.id = :userId)")
    fun countByUserId(userId: Long): Long

    @Query("SELECT AVG(gr.answerTimeMs) FROM GameRound gr WHERE gr.player.id = :userId AND gr.answerTimeMs IS NOT NULL")
    fun avgAnswerTimeByUserId(userId: Long): Double?
}

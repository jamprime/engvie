package com.engvie.repository

import com.engvie.model.entity.MatchmakingQueue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface MatchmakingQueueRepository : JpaRepository<MatchmakingQueue, Int> {
    fun findByUserIdAndStatus(userId: Long, status: String): MatchmakingQueue?

    fun findAllByStatus(status: String): List<MatchmakingQueue>

    @Query("SELECT mq FROM MatchmakingQueue mq WHERE mq.status = 'searching' AND mq.joinedAt <= :cutoff")
    fun findTimedOutEntries(cutoff: LocalDateTime): List<MatchmakingQueue>

    @Query(value = """
        SELECT * FROM matchmaking_queue
        WHERE status = 'searching'
        AND user_id != :excludeUserId
        AND rating BETWEEN :minRating AND :maxRating
        ORDER BY ABS(rating - :targetRating)
        LIMIT 1
    """, nativeQuery = true)
    fun findOpponent(excludeUserId: Long, minRating: Int, maxRating: Int, targetRating: Int): MatchmakingQueue?
}

package com.engvie.service

import com.engvie.model.entity.MatchmakingQueue
import com.engvie.model.entity.User
import com.engvie.repository.MatchmakingQueueRepository
import com.engvie.util.Constants
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MatchmakingService(
    private val matchmakingQueueRepository: MatchmakingQueueRepository
) {
    @Transactional
    fun joinQueue(user: User, roundsCount: Int): MatchmakingQueue {
        // Cancel existing queue entry if any
        val existing = matchmakingQueueRepository.findByUserIdAndStatus(user.id, "searching")
        existing?.let {
            it.status = "cancelled"
            matchmakingQueueRepository.save(it)
        }

        return matchmakingQueueRepository.save(
            MatchmakingQueue(
                user = user,
                rating = user.rating,
                roundsPreference = roundsCount
            )
        )
    }

    @Transactional
    fun cancelQueue(userId: Long) {
        val entry = matchmakingQueueRepository.findByUserIdAndStatus(userId, "searching")
        entry?.let {
            it.status = "cancelled"
            matchmakingQueueRepository.save(it)
        }
    }

    @Transactional
    fun findOpponent(userId: Long, userRating: Int): MatchmakingQueue? {
        return matchmakingQueueRepository.findOpponent(
            excludeUserId = userId,
            minRating = userRating - Constants.MATCHMAKING_RATING_RANGE,
            maxRating = userRating + Constants.MATCHMAKING_RATING_RANGE,
            targetRating = userRating
        )
    }

    @Transactional
    fun markMatched(queueEntry: MatchmakingQueue) {
        queueEntry.status = "matched"
        matchmakingQueueRepository.save(queueEntry)
    }
}

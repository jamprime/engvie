package com.engvie.service

import com.engvie.model.dto.response.LeaderboardEntry
import com.engvie.model.dto.response.LeaderboardResponse
import com.engvie.repository.UserRepository
import com.engvie.util.Constants
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class LeaderboardService(
    private val userRepository: UserRepository
) {
    @Cacheable(value = ["leaderboard"], key = "'global'")
    fun getGlobalLeaderboard(): List<LeaderboardEntry> {
        val users = userRepository.findTopByRating(PageRequest.of(0, 100))
        return users.mapIndexed { index, user ->
            LeaderboardEntry(
                rank = index + 1,
                userId = user.id,
                username = user.username,
                rating = user.rating,
                rankTitle = user.rank,
                wins = user.wins
            )
        }
    }

    fun getLeaderboardForUser(userId: Long): LeaderboardResponse {
        val leaderboard = getGlobalLeaderboard()
        val myPosition = leaderboard.indexOfFirst { it.userId == userId }
            .let { if (it == -1) userRepository.countUsersWithHigherRating(
                userRepository.findById(userId).orElse(null)?.rating ?: 0
            ).toInt() + 1 else it + 1 }

        return LeaderboardResponse(leaderboard = leaderboard, myPosition = myPosition)
    }
}

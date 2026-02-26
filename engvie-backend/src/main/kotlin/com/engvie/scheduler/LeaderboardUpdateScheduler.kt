package com.engvie.scheduler

import com.engvie.service.LeaderboardService
import org.slf4j.LoggerFactory
import org.springframework.cache.CacheManager
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class LeaderboardUpdateScheduler(
    private val cacheManager: CacheManager
) {
    private val logger = LoggerFactory.getLogger(LeaderboardUpdateScheduler::class.java)

    @Scheduled(fixedDelay = 600000) // Every 10 minutes
    fun refreshLeaderboard() {
        logger.info("Refreshing leaderboard cache")
        cacheManager.getCache("leaderboard")?.evict("global")
    }
}

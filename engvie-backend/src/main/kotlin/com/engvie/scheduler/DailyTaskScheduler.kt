package com.engvie.scheduler

import com.engvie.service.DailyTaskService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DailyTaskScheduler(private val dailyTaskService: DailyTaskService) {
    private val logger = LoggerFactory.getLogger(DailyTaskScheduler::class.java)

    @Scheduled(cron = "0 0 0 * * *") // Every day at midnight UTC
    fun generateDailyTasks() {
        logger.info("Generating daily tasks")
        dailyTaskService.generateDailyTasks()
    }
}

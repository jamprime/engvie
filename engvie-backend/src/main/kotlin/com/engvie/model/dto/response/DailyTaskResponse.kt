package com.engvie.model.dto.response

import com.engvie.model.entity.DailyTask
import com.engvie.model.entity.UserDailyTask
import java.time.LocalDate
import java.time.LocalDateTime

data class DailyTaskResponse(
    val id: Int,
    val date: LocalDate,
    val taskType: String,
    val requirement: Int,
    val rewardCoins: Int,
    val progress: Int,
    val completed: Boolean,
    val completedAt: LocalDateTime?
) {
    companion object {
        fun from(task: DailyTask, userTask: UserDailyTask?) = DailyTaskResponse(
            id = task.id,
            date = task.date,
            taskType = task.taskType,
            requirement = task.requirement,
            rewardCoins = task.rewardCoins,
            progress = userTask?.progress ?: 0,
            completed = userTask?.completed ?: false,
            completedAt = userTask?.completedAt
        )
    }
}

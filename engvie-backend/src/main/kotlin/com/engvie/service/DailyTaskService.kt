package com.engvie.service

import com.engvie.model.dto.response.DailyTaskResponse
import com.engvie.model.entity.DailyTask
import com.engvie.model.entity.UserDailyTask
import com.engvie.repository.DailyTaskRepository
import com.engvie.repository.UserDailyTaskRepository
import com.engvie.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class DailyTaskService(
    private val dailyTaskRepository: DailyTaskRepository,
    private val userDailyTaskRepository: UserDailyTaskRepository,
    private val userRepository: UserRepository
) {
    fun getTodaysTasks(userId: Long): List<DailyTaskResponse> {
        val today = LocalDate.now()
        val tasks = dailyTaskRepository.findByDate(today)
        val userTasks = userDailyTaskRepository.findByUserIdAndDate(userId, today)
            .associateBy { it.dailyTask.id }

        return tasks.map { task ->
            DailyTaskResponse.from(task, userTasks[task.id])
        }
    }

    @Transactional
    fun generateDailyTasks() {
        val today = LocalDate.now()
        if (dailyTaskRepository.existsByDate(today)) return

        val tasks = listOf(
            DailyTask(date = today, taskType = "win_games", requirement = 3, rewardCoins = 50),
            DailyTask(date = today, taskType = "play_games", requirement = 5, rewardCoins = 30),
            DailyTask(date = today, taskType = "learn_words", requirement = 10, rewardCoins = 40),
            DailyTask(date = today, taskType = "correct_answers", requirement = 20, rewardCoins = 60)
        )
        dailyTaskRepository.saveAll(tasks)
    }

    @Transactional
    fun updateProgress(userId: Long, taskType: String, amount: Int = 1): List<DailyTaskResponse> {
        val today = LocalDate.now()
        val tasks = dailyTaskRepository.findByDate(today).filter { it.taskType == taskType }

        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        tasks.forEach { task ->
            val userTask = userDailyTaskRepository.findByUserIdAndDailyTaskId(userId, task.id)
                ?: UserDailyTask(user = user, dailyTask = task)

            if (!userTask.completed) {
                userTask.progress = minOf(userTask.progress + amount, task.requirement)
                if (userTask.progress >= task.requirement) {
                    userTask.completed = true
                    userTask.completedAt = LocalDateTime.now()
                }
                userDailyTaskRepository.save(userTask)
            }
        }

        return getTodaysTasks(userId)
    }

    @Transactional
    fun claimReward(userId: Long, taskId: Int, userService: UserService): Int {
        val task = dailyTaskRepository.findById(taskId).orElseThrow { RuntimeException("Task not found") }
        val userTask = userDailyTaskRepository.findByUserIdAndDailyTaskId(userId, taskId)
            ?: throw RuntimeException("Task not started")

        if (!userTask.completed) throw IllegalStateException("Task not completed yet")
        if (userTask.completedAt == null) {
            // Already claimed check would need a separate field, for now use completedAt as claimed marker
        }

        val user = userService.getUserById(userId)
        user.coins += task.rewardCoins
        userService.save(user)

        return task.rewardCoins
    }
}

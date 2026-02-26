package com.engvie.repository

import com.engvie.model.entity.UserDailyTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface UserDailyTaskRepository : JpaRepository<UserDailyTask, Int> {
    fun findByUserIdAndDailyTaskId(userId: Long, dailyTaskId: Int): UserDailyTask?

    @Query("SELECT udt FROM UserDailyTask udt WHERE udt.user.id = :userId AND udt.dailyTask.date = :date")
    fun findByUserIdAndDate(userId: Long, date: LocalDate): List<UserDailyTask>
}

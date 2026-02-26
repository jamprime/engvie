package com.engvie.repository

import com.engvie.model.entity.DailyTask
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DailyTaskRepository : JpaRepository<DailyTask, Int> {
    fun findByDate(date: LocalDate): List<DailyTask>
    fun existsByDate(date: LocalDate): Boolean
}

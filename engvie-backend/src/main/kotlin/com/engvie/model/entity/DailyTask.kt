package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(
    name = "daily_tasks",
    uniqueConstraints = [UniqueConstraint(columnNames = ["date", "task_type"])]
)
data class DailyTask(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false)
    val date: LocalDate = LocalDate.now(),

    @Column(name = "task_type", nullable = false, length = 100)
    val taskType: String = "",

    @Column(nullable = false)
    val requirement: Int = 0,

    @Column(name = "reward_coins", nullable = false)
    val rewardCoins: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

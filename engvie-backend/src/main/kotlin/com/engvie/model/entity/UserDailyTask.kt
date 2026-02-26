package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_daily_tasks",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "daily_task_id"])],
    indexes = [Index(name = "idx_user_date", columnList = "user_id, completed")]
)
data class UserDailyTask(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User = User(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "daily_task_id", nullable = false)
    val dailyTask: DailyTask = DailyTask(),

    @Column(nullable = false)
    var progress: Int = 0,

    @Column(nullable = false)
    var completed: Boolean = false,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime? = null
)

package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_achievements",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "achievement_id"])],
    indexes = [Index(name = "idx_user_achievement", columnList = "user_id")]
)
data class UserAchievement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User = User(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "achievement_id", nullable = false)
    val achievement: Achievement = Achievement(),

    @Column(name = "unlocked_at", nullable = false)
    val unlockedAt: LocalDateTime = LocalDateTime.now()
)

package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "matchmaking_queue",
    indexes = [
        Index(name = "idx_rating_status", columnList = "rating, status"),
        Index(name = "idx_user_status", columnList = "user_id, status")
    ]
)
data class MatchmakingQueue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User = User(),

    @Column(nullable = false)
    val rating: Int = 0,

    @Column(name = "rounds_preference", nullable = false)
    val roundsPreference: Int = 3,

    @Column(name = "joined_at", nullable = false)
    val joinedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false, length = 50)
    var status: String = "searching"
)

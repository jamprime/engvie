package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "games",
    indexes = [
        Index(name = "idx_player1", columnList = "player1_id"),
        Index(name = "idx_player2", columnList = "player2_id"),
        Index(name = "idx_status", columnList = "status")
    ]
)
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", nullable = false)
    val player1: User = User(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    val player2: User? = null,

    @Column(name = "player1_score", nullable = false)
    var player1Score: Int = 0,

    @Column(name = "player2_score", nullable = false)
    var player2Score: Int = 0,

    @Column(name = "winner_id")
    var winnerId: Long? = null,

    @Column(name = "game_type", nullable = false, length = 50)
    val gameType: String = "ranked",

    @Column(name = "rounds_count", nullable = false)
    val roundsCount: Int = 3,

    @Column(nullable = false, length = 50)
    var status: String = "waiting",

    @Column(name = "current_turn")
    var currentTurn: Long? = null,

    @Column(name = "is_bot_game", nullable = false)
    val isBotGame: Boolean = false,

    @Column(name = "bot_difficulty", length = 50)
    val botDifficulty: String? = null,

    @Column(name = "started_at")
    var startedAt: LocalDateTime? = null,

    @Column(name = "finished_at")
    var finishedAt: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

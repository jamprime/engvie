package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "game_rounds",
    indexes = [Index(name = "idx_game", columnList = "game_id")]
)
data class GameRound(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    val game: Game = Game(),

    @Column(name = "round_number", nullable = false)
    val roundNumber: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    val player: User = User(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    val word: Word = Word(),

    @Column(nullable = false, length = 10)
    val direction: String = "en_to_ru",

    @Column(columnDefinition = "text")
    val options: String = "[]",

    @Column(name = "correct_answer", nullable = false, length = 255)
    val correctAnswer: String = "",

    @Column(name = "user_answer", length = 255)
    var userAnswer: String? = null,

    @Column(name = "is_correct")
    var isCorrect: Boolean? = null,

    @Column(name = "answer_time_ms")
    var answerTimeMs: Int? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

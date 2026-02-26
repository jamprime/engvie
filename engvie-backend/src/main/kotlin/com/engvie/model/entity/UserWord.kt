package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_words",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "word_id"])],
    indexes = [Index(name = "idx_user_next_review", columnList = "user_id, next_review")]
)
data class UserWord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User = User(),

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "word_id", nullable = false)
    val word: Word = Word(),

    @Column(nullable = false, length = 50)
    var status: String = "learning",

    @Column(name = "correct_count", nullable = false)
    var correctCount: Int = 0,

    @Column(name = "incorrect_count", nullable = false)
    var incorrectCount: Int = 0,

    @Column(name = "last_reviewed")
    var lastReviewed: LocalDateTime? = null,

    @Column(name = "next_review", nullable = false)
    var nextReview: LocalDateTime = LocalDateTime.now(),

    @Column(name = "ease_factor", nullable = false)
    var easeFactor: Double = 2.5,

    @Column(name = "interval_days", nullable = false)
    var intervalDays: Int = 1,

    @Column(name = "added_from", length = 50)
    val addedFrom: String = "manual",

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

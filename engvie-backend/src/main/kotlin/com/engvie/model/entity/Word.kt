package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "words",
    indexes = [
        Index(name = "idx_level", columnList = "level"),
        Index(name = "idx_category", columnList = "category")
    ]
)
data class Word(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(nullable = false, unique = true, length = 255)
    val english: String = "",

    @Column(nullable = false, length = 255)
    val russian: String = "",

    @Column(nullable = false, length = 10)
    val level: String = "A1",

    @Column(length = 100)
    val category: String? = null,

    @Column
    val frequency: Int? = null,

    @Column(name = "audio_url", length = 500)
    val audioUrl: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "word_categories")
data class WordCategory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(unique = true, nullable = false, length = 100)
    val code: String = "",

    @Column(name = "name_en", length = 255)
    val nameEn: String = "",

    @Column(name = "name_ru", length = 255)
    val nameRu: String = "",

    @Column(name = "description_ru", columnDefinition = "TEXT")
    val descriptionRu: String? = null,

    @Column(length = 10)
    val icon: String = "",

    @Column(name = "words_count", nullable = false)
    var wordsCount: Int = 0,

    @Column(name = "min_level", length = 10)
    val minLevel: String = "A1",

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

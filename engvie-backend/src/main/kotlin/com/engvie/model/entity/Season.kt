package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "seasons")
data class Season(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(name = "season_number", unique = true, nullable = false)
    val seasonNumber: Int = 0,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate = LocalDate.now(),

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDate = LocalDate.now(),

    @Column(name = "is_active", nullable = false)
    var isActive: Boolean = false,

    @Column(name = "rating_decay_percent", nullable = false)
    val ratingDecayPercent: Int = 50,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

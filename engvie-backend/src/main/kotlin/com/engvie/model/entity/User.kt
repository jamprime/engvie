package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    val id: Long = 0,

    @Column(nullable = false, length = 255)
    var username: String = "",

    @Column(name = "first_name", nullable = false, length = 255)
    var firstName: String = "",

    @Column(name = "last_name", length = 255)
    var lastName: String? = null,

    @Column(name = "language_code", length = 10)
    var languageCode: String = "ru",

    @Column(nullable = false)
    var rating: Int = 500,

    @Column(nullable = false, length = 50)
    var rank: String = "SILVER",

    @Column(nullable = false, length = 10)
    var level: String = "A2",

    @Column(name = "total_games", nullable = false)
    var totalGames: Int = 0,

    @Column(nullable = false)
    var wins: Int = 0,

    @Column(nullable = false)
    var losses: Int = 0,

    @Column(nullable = false)
    var draws: Int = 0,

    @Column(nullable = false)
    var energy: Int = 5,

    @Column(name = "max_energy", nullable = false)
    var maxEnergy: Int = 5,

    @Column(name = "last_energy_update")
    var lastEnergyUpdate: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var coins: Int = 0,

    @Column(name = "streak_days", nullable = false)
    var streakDays: Int = 0,

    @Column(name = "last_login_date")
    var lastLoginDate: LocalDate? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

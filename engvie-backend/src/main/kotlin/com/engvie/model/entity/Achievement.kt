package com.engvie.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "achievements")
data class Achievement(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    @Column(unique = true, nullable = false, length = 100)
    val code: String = "",

    @Column(name = "name_en", length = 255)
    val nameEn: String = "",

    @Column(name = "name_ru", length = 255)
    val nameRu: String = "",

    @Column(name = "description_en", columnDefinition = "TEXT")
    val descriptionEn: String? = null,

    @Column(name = "description_ru", columnDefinition = "TEXT")
    val descriptionRu: String? = null,

    @Column(length = 255)
    val icon: String = "",

    @Column(name = "reward_coins", nullable = false)
    val rewardCoins: Int = 0,

    @Column(name = "condition_type", length = 100)
    val conditionType: String = "",

    @Column(name = "condition_value")
    val conditionValue: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

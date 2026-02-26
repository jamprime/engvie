package com.engvie.repository

import com.engvie.model.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?

    @Query("SELECT u FROM User u ORDER BY u.rating DESC")
    fun findTopByRating(pageable: Pageable): List<User>

    @Query("SELECT COUNT(u) FROM User u WHERE u.rating > :rating")
    fun countUsersWithHigherRating(rating: Int): Long

    @Query("SELECT u FROM User u WHERE u.energy < u.maxEnergy AND u.lastEnergyUpdate < :threshold")
    fun findUsersNeedingEnergyRecovery(threshold: LocalDateTime): List<User>
}

package com.engvie.repository

import com.engvie.model.entity.UserWord
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserWordRepository : JpaRepository<UserWord, Int> {
    fun findByUserIdAndWordId(userId: Long, wordId: Int): UserWord?

    fun findByUserId(userId: Long): List<UserWord>

    fun findByUserIdAndStatus(userId: Long, status: String): List<UserWord>

    @Query("SELECT uw FROM UserWord uw WHERE uw.user.id = :userId AND uw.nextReview <= :now ORDER BY uw.nextReview")
    fun findDueForReview(userId: Long, now: LocalDateTime): List<UserWord>

    @Query("SELECT COUNT(uw) FROM UserWord uw WHERE uw.user.id = :userId AND uw.nextReview <= :now")
    fun countDueForReview(userId: Long, now: LocalDateTime): Long

    @Query("SELECT COUNT(uw) FROM UserWord uw WHERE uw.user.id = :userId")
    fun countByUserId(userId: Long): Long

    @Query("SELECT uw FROM UserWord uw WHERE uw.user.id = :userId AND uw.word.category = :category")
    fun findByUserIdAndCategory(userId: Long, category: String): List<UserWord>

    @Query("SELECT uw FROM UserWord uw WHERE uw.user.id = :userId AND uw.addedFrom = 'battle_error' ORDER BY uw.updatedAt DESC")
    fun findBattleMistakes(userId: Long): List<UserWord>
}

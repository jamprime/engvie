package com.engvie.repository

import com.engvie.model.entity.Word
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface WordRepository : JpaRepository<Word, Int> {
    fun findByLevel(level: String): List<Word>

    fun findByCategory(category: String): List<Word>

    fun findByLevelAndCategory(level: String, category: String): List<Word>

    @Query(value = "SELECT * FROM words WHERE level = :level ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    fun findRandomByLevel(level: String, count: Int): List<Word>

    @Query(value = "SELECT * FROM words WHERE level IN (:levels) ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    fun findRandomByLevels(levels: List<String>, count: Int): List<Word>

    @Query(value = "SELECT * FROM words WHERE level = :level AND id != :excludeId ORDER BY RANDOM() LIMIT :count", nativeQuery = true)
    fun findRandomByLevelExcluding(level: String, excludeId: Int, count: Int): List<Word>
}

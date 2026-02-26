package com.engvie.repository

import com.engvie.model.entity.WordCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WordCategoryRepository : JpaRepository<WordCategory, Int> {
    fun findByCode(code: String): WordCategory?
}

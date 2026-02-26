package com.engvie.repository

import com.engvie.model.entity.Season
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SeasonRepository : JpaRepository<Season, Int> {
    fun findByIsActiveTrue(): Season?
}

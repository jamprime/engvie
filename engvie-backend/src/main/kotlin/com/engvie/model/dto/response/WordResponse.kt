package com.engvie.model.dto.response

import com.engvie.model.entity.UserWord
import com.engvie.model.entity.Word
import java.time.LocalDateTime

data class WordResponse(
    val id: Int,
    val english: String,
    val russian: String,
    val level: String,
    val category: String?
)

data class UserWordResponse(
    val id: Int,
    val word: WordResponse,
    val status: String,
    val correctCount: Int,
    val incorrectCount: Int,
    val lastReviewed: LocalDateTime?,
    val nextReview: LocalDateTime,
    val easeFactor: Double,
    val intervalDays: Int,
    val addedFrom: String
) {
    companion object {
        fun from(uw: UserWord) = UserWordResponse(
            id = uw.id,
            word = WordResponse(
                id = uw.word.id,
                english = uw.word.english,
                russian = uw.word.russian,
                level = uw.word.level,
                category = uw.word.category
            ),
            status = uw.status,
            correctCount = uw.correctCount,
            incorrectCount = uw.incorrectCount,
            lastReviewed = uw.lastReviewed,
            nextReview = uw.nextReview,
            easeFactor = uw.easeFactor,
            intervalDays = uw.intervalDays,
            addedFrom = uw.addedFrom
        )
    }
}

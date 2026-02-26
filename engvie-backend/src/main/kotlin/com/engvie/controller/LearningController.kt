package com.engvie.controller

import com.engvie.model.dto.request.WordReviewRequest
import com.engvie.model.dto.response.UserWordResponse
import com.engvie.model.dto.response.WordCategoryResponse
import com.engvie.model.dto.response.WordResponse
import com.engvie.service.LearningService
import com.engvie.service.WordService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/words")
class LearningController(
    private val learningService: LearningService,
    private val wordService: WordService
) {
    @GetMapping("/my")
    fun getMyWords(
        auth: Authentication,
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) category: String?
    ): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val words = learningService.getMyWords(userId, status, category)
        return ResponseEntity.ok(mapOf("words" to words, "total" to words.size))
    }

    @PostMapping("/my/{wordId}")
    fun addWord(
        auth: Authentication,
        @PathVariable wordId: Int,
        @RequestParam(defaultValue = "manual") addedFrom: String
    ): ResponseEntity<UserWordResponse> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(learningService.addWord(userId, wordId, addedFrom))
    }

    @DeleteMapping("/my/{wordId}")
    fun removeWord(
        auth: Authentication,
        @PathVariable wordId: Int
    ): ResponseEntity<Map<String, Boolean>> {
        val userId = auth.principal as Long
        learningService.removeWord(userId, wordId)
        return ResponseEntity.ok(mapOf("success" to true))
    }

    @GetMapping("/due")
    fun getDueWords(auth: Authentication): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val words = learningService.getDueWords(userId)
        return ResponseEntity.ok(mapOf("words" to words))
    }

    @PostMapping("/{wordId}/review")
    fun reviewWord(
        auth: Authentication,
        @PathVariable wordId: Int,
        @RequestBody request: WordReviewRequest
    ): ResponseEntity<UserWordResponse> {
        val userId = auth.principal as Long
        return ResponseEntity.ok(learningService.reviewWord(userId, wordId, request.quality))
    }

    @GetMapping("/categories")
    fun getCategories(): ResponseEntity<Map<String, Any>> {
        val categories = wordService.getCategories()
        return ResponseEntity.ok(mapOf("categories" to categories))
    }

    @GetMapping("/categories/{code}")
    fun getCategoryWords(@PathVariable code: String): ResponseEntity<Map<String, Any>> {
        val (category, words) = wordService.getCategoryWithWords(code)
        return ResponseEntity.ok(mapOf("category" to category, "words" to words))
    }

    @GetMapping("/mistakes")
    fun getBattleMistakes(auth: Authentication): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val words = learningService.getBattleMistakes(userId)
        return ResponseEntity.ok(mapOf("words" to words))
    }
}

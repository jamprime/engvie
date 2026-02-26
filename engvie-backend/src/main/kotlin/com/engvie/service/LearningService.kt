package com.engvie.service

import com.engvie.model.dto.response.UserWordResponse
import com.engvie.model.entity.UserWord
import com.engvie.repository.UserWordRepository
import com.engvie.repository.WordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class LearningService(
    private val userWordRepository: UserWordRepository,
    private val wordRepository: WordRepository,
    private val spacedRepetitionService: SpacedRepetitionService,
    private val userService: UserService
) {
    fun getMyWords(userId: Long, status: String?, category: String?): List<UserWordResponse> {
        val words = when {
            status != null && category != null -> {
                userWordRepository.findByUserIdAndStatus(userId, status)
                    .filter { it.word.category == category }
            }
            status != null -> userWordRepository.findByUserIdAndStatus(userId, status)
            category != null -> userWordRepository.findByUserIdAndCategory(userId, category)
            else -> userWordRepository.findByUserId(userId)
        }
        return words.map { UserWordResponse.from(it) }
    }

    fun getDueWords(userId: Long): List<UserWordResponse> {
        return userWordRepository.findDueForReview(userId, LocalDateTime.now())
            .map { UserWordResponse.from(it) }
    }

    fun getBattleMistakes(userId: Long): List<UserWordResponse> {
        return userWordRepository.findBattleMistakes(userId)
            .map { UserWordResponse.from(it) }
    }

    @Transactional
    fun addWord(userId: Long, wordId: Int, addedFrom: String): UserWordResponse {
        val existing = userWordRepository.findByUserIdAndWordId(userId, wordId)
        if (existing != null) return UserWordResponse.from(existing)

        val user = userService.getUserById(userId)
        val word = wordRepository.findById(wordId).orElseThrow { RuntimeException("Word not found") }

        val userWord = userWordRepository.save(
            UserWord(
                user = user,
                word = word,
                addedFrom = addedFrom
            )
        )
        return UserWordResponse.from(userWord)
    }

    @Transactional
    fun addWordsFromBattle(userId: Long, wordIds: List<Int>) {
        wordIds.forEach { wordId ->
            try {
                addWord(userId, wordId, "battle_error")
            } catch (e: Exception) {
                // Skip if word already exists or not found
            }
        }
    }

    @Transactional
    fun removeWord(userId: Long, wordId: Int) {
        val userWord = userWordRepository.findByUserIdAndWordId(userId, wordId)
            ?: throw RuntimeException("Word not found in your dictionary")
        userWordRepository.delete(userWord)
    }

    @Transactional
    fun reviewWord(userId: Long, wordId: Int, quality: Int): UserWordResponse {
        val userWord = userWordRepository.findByUserIdAndWordId(userId, wordId)
            ?: throw RuntimeException("Word not found in your dictionary")

        val updated = spacedRepetitionService.updateReview(userWord, quality)
        return UserWordResponse.from(userWordRepository.save(updated))
    }
}

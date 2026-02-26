package com.engvie.service

import com.engvie.model.dto.response.WordCategoryResponse
import com.engvie.model.dto.response.WordResponse
import com.engvie.model.entity.Word
import com.engvie.repository.WordCategoryRepository
import com.engvie.repository.WordRepository
import org.springframework.stereotype.Service

@Service
class WordService(
    private val wordRepository: WordRepository,
    private val wordCategoryRepository: WordCategoryRepository
) {
    fun getCategories(): List<WordCategoryResponse> {
        return wordCategoryRepository.findAll()
            .map { WordCategoryResponse.from(it) }
    }

    fun getCategoryWithWords(code: String): Pair<WordCategoryResponse, List<WordResponse>> {
        val category = wordCategoryRepository.findByCode(code)
            ?: throw RuntimeException("Category not found: $code")
        val words = wordRepository.findByCategory(code)
            .map { WordResponse(it.id, it.english, it.russian, it.level, it.category) }
        return WordCategoryResponse.from(category) to words
    }

    fun getRandomWordsForLevel(level: String, count: Int): List<Word> {
        return wordRepository.findRandomByLevel(level, count)
    }

    fun getRandomWrongOptions(correctWord: Word, level: String, count: Int, direction: String): List<String> {
        val candidates = wordRepository.findRandomByLevelExcluding(level, correctWord.id, count * 3)
        return candidates.shuffled().take(count).map { word ->
            if (direction == "en_to_ru") word.russian else word.english
        }
    }
}

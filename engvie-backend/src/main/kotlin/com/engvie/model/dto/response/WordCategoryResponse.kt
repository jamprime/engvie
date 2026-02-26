package com.engvie.model.dto.response

import com.engvie.model.entity.WordCategory

data class WordCategoryResponse(
    val id: Int,
    val code: String,
    val nameEn: String,
    val nameRu: String,
    val descriptionRu: String?,
    val icon: String,
    val wordsCount: Int,
    val minLevel: String
) {
    companion object {
        fun from(category: WordCategory) = WordCategoryResponse(
            id = category.id,
            code = category.code,
            nameEn = category.nameEn,
            nameRu = category.nameRu,
            descriptionRu = category.descriptionRu,
            icon = category.icon,
            wordsCount = category.wordsCount,
            minLevel = category.minLevel
        )
    }
}

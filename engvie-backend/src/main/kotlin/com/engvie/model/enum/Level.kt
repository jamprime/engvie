package com.engvie.model.enum

enum class Level(val minRating: Int) {
    A1(0),
    A2(500),
    B1(1000),
    B2(1500),
    C1(2000),
    C2(2500);

    companion object {
        fun fromRating(rating: Int): Level {
            return entries.reversed().firstOrNull { rating >= it.minRating } ?: A1
        }
    }
}

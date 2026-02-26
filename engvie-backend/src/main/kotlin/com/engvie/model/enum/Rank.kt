package com.engvie.model.enum

enum class Rank(val minRating: Int, val maxRating: Int) {
    BRONZE(0, 499),
    SILVER(500, 999),
    GOLD(1000, 1499),
    PLATINUM(1500, 1999),
    DIAMOND(2000, 2499),
    MASTER(2500, 2999),
    GRANDMASTER(3000, Int.MAX_VALUE);

    companion object {
        fun fromRating(rating: Int): Rank {
            return entries.reversed().firstOrNull { rating >= it.minRating } ?: BRONZE
        }
    }
}

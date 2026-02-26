package com.engvie.service

import com.engvie.util.Constants
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class RatingService {
    fun calculateWinChange(playerRating: Int, opponentRating: Int, isBotGame: Boolean): Int {
        if (isBotGame) return 0
        val change = Constants.WIN_BASE_CHANGE + (opponentRating - playerRating) / 50
        return change.coerceIn(Constants.MIN_WIN_CHANGE, Constants.MAX_WIN_CHANGE)
    }

    fun calculateLossChange(playerRating: Int, opponentRating: Int, isBotGame: Boolean): Int {
        if (isBotGame) return 0
        val change = Constants.LOSS_BASE_CHANGE + (playerRating - opponentRating) / 50
        return change.coerceIn(Constants.MIN_LOSS_CHANGE, Constants.MAX_LOSS_CHANGE)
    }

    fun calculateDrawChange(isBotGame: Boolean): Int {
        return if (isBotGame) 0 else Constants.DRAW_CHANGE
    }
}

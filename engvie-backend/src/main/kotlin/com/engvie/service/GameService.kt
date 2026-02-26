package com.engvie.service

import com.engvie.model.dto.response.GameResponse
import com.engvie.model.entity.Game
import com.engvie.model.entity.GameRound
import com.engvie.model.entity.User
import com.engvie.model.enum.BotDifficulty
import com.engvie.model.enum.Level
import com.engvie.repository.GameRepository
import com.engvie.repository.GameRoundRepository
import com.engvie.repository.UserRepository
import com.engvie.util.Constants
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val gameRoundRepository: GameRoundRepository,
    private val userRepository: UserRepository,
    private val wordService: WordService,
    private val ratingService: RatingService,
    private val objectMapper: ObjectMapper
) {
    @Transactional
    fun createComputerGame(player1: User, difficulty: String, roundsCount: Int): Game {
        val game = gameRepository.save(
            Game(
                player1 = player1,
                gameType = "computer",
                roundsCount = roundsCount,
                status = "in_progress",
                isBotGame = true,
                botDifficulty = difficulty,
                startedAt = LocalDateTime.now(),
                currentTurn = player1.id
            )
        )
        generateRounds(game, player1, null, roundsCount)
        return gameRepository.save(game)
    }

    @Transactional
    fun createRankedGame(player1: User, player2: User, roundsCount: Int): Game {
        val firstPlayer = if (Random.nextBoolean()) player1 else player2
        val game = gameRepository.save(
            Game(
                player1 = player1,
                player2 = player2,
                gameType = "ranked",
                roundsCount = roundsCount,
                status = "in_progress",
                isBotGame = false,
                startedAt = LocalDateTime.now(),
                currentTurn = firstPlayer.id
            )
        )
        generateRounds(game, player1, player2, roundsCount)
        return gameRepository.save(game)
    }

    private fun generateRounds(game: Game, player1: User, player2: User?, roundsCount: Int) {
        val level = Level.fromRating(player1.rating).name
        val players = if (player2 != null) listOf(player1, player2) else listOf(player1)

        players.forEach { player ->
            repeat(roundsCount) { roundNumber ->
                val words = wordService.getRandomWordsForLevel(level, 1)
                val word = words.firstOrNull() ?: return@repeat
                val direction = if (Random.nextBoolean()) "en_to_ru" else "ru_to_en"
                val correctAnswer = if (direction == "en_to_ru") word.russian else word.english
                val wrongOptions = wordService.getRandomWrongOptions(word, level, 3, direction)
                val allOptions = (listOf(correctAnswer) + wrongOptions).shuffled()

                gameRoundRepository.save(
                    GameRound(
                        game = game,
                        roundNumber = roundNumber + 1,
                        player = player,
                        word = word,
                        direction = direction,
                        options = objectMapper.writeValueAsString(allOptions),
                        correctAnswer = correctAnswer
                    )
                )
            }
        }
    }

    @Transactional
    fun submitAnswer(
        gameId: Int,
        roundId: Int,
        playerId: Long,
        answer: String,
        answerTimeMs: Int
    ): AnswerResult {
        val game = gameRepository.findByIdForUpdate(gameId) ?: throw RuntimeException("Game not found")
        val round = gameRoundRepository.findById(roundId).orElseThrow { RuntimeException("Round not found") }

        if (round.player.id != playerId) throw IllegalStateException("Not your round")
        if (round.userAnswer != null) throw IllegalStateException("Already answered")
        if (game.currentTurn != null && game.currentTurn != playerId) throw IllegalStateException("Not your turn")

        val isCorrect = answer == round.correctAnswer
        round.userAnswer = answer
        round.isCorrect = isCorrect
        round.answerTimeMs = answerTimeMs
        gameRoundRepository.save(round)

        if (isCorrect) {
            if (round.player.id == game.player1.id) game.player1Score++
            else game.player2Score++
        }

        // Switch turn
        game.currentTurn = when {
            game.isBotGame -> null  // null = bot's turn, handled by handler
            game.currentTurn == game.player1.id -> game.player2?.id
            else -> game.player1.id
        }

        // For PvP: check game completion (bot games checked after bot simulated turn)
        val allDone = if (!game.isBotGame) {
            val p1Rounds = gameRoundRepository.countAnsweredRounds(gameId, game.player1.id)
            val p2Rounds = game.player2?.id?.let { gameRoundRepository.countAnsweredRounds(gameId, it) } ?: 0L
            p1Rounds >= game.roundsCount && p2Rounds >= game.roundsCount
        } else false

        val gameFinished = if (allDone) finishGame(game) else null
        gameRepository.save(game)

        return AnswerResult(
            isCorrect = isCorrect,
            correctAnswer = round.correctAnswer,
            currentScore = Pair(game.player1Score, game.player2Score),
            gameFinished = gameFinished,
            nextTurnPlayerId = if (gameFinished != null) null else game.currentTurn
        )
    }

    @Transactional
    fun simulateBotTurn(gameId: Int): BotTurnResult {
        val game = gameRepository.findByIdForUpdate(gameId) ?: throw RuntimeException("Game not found")
        val botDifficulty = BotDifficulty.valueOf(game.botDifficulty!!.uppercase())
        val isCorrect = Random.nextDouble() < botDifficulty.correctAnswerRate

        if (isCorrect) game.player2Score++

        // Switch back to player
        game.currentTurn = game.player1.id

        // Game is done when player has answered all rounds (bot always "answers" same number as player)
        val playerRoundsAnswered = gameRoundRepository.countAnsweredRounds(gameId, game.player1.id)
        val allDone = playerRoundsAnswered >= game.roundsCount

        val gameFinished = if (allDone) finishGame(game) else null
        gameRepository.save(game)

        return BotTurnResult(
            isCorrect = isCorrect,
            currentScore = Pair(game.player1Score, game.player2Score),
            gameFinished = gameFinished
        )
    }

    private fun finishGame(game: Game): GameFinishResult {
        game.status = "finished"
        game.finishedAt = LocalDateTime.now()

        val player1 = game.player1
        val result: String
        val p1RatingChange: Int
        var p2RatingChange = 0
        val winnerId: Long?

        val opponentRating = game.player2?.rating ?: 0
        val botDiff = if (game.isBotGame) game.botDifficulty?.let { BotDifficulty.valueOf(it.uppercase()) } else null

        val winCoins  = botDiff?.winCoins  ?: Constants.WIN_COINS
        val drawCoins = botDiff?.drawCoins ?: Constants.DRAW_COINS
        val lossCoins = botDiff?.lossCoins ?: Constants.LOSS_COINS

        val coinsEarned: Int

        when {
            game.player1Score > game.player2Score -> {
                game.winnerId = player1.id
                winnerId = player1.id
                result = "win"
                coinsEarned = winCoins
                p1RatingChange = ratingService.calculateWinChange(player1.rating, opponentRating, game.isBotGame)
                player1.wins++
                player1.coins += winCoins
                game.player2?.let {
                    p2RatingChange = ratingService.calculateLossChange(it.rating, player1.rating, false)
                    it.losses++; it.coins += Constants.LOSS_COINS; it.totalGames++
                    it.rating = maxOf(0, it.rating + p2RatingChange)
                    userRepository.save(it)
                }
            }
            game.player1Score < game.player2Score -> {
                game.winnerId = game.player2?.id
                winnerId = game.player2?.id
                result = "loss"
                coinsEarned = lossCoins
                p1RatingChange = ratingService.calculateLossChange(player1.rating, opponentRating, game.isBotGame)
                player1.losses++
                player1.coins += lossCoins
                game.player2?.let {
                    p2RatingChange = ratingService.calculateWinChange(it.rating, player1.rating, false)
                    it.wins++; it.coins += Constants.WIN_COINS; it.totalGames++
                    it.rating = maxOf(0, it.rating + p2RatingChange)
                    userRepository.save(it)
                }
            }
            else -> {
                winnerId = null
                result = "tie"
                coinsEarned = drawCoins
                p1RatingChange = ratingService.calculateDrawChange(game.isBotGame)
                player1.draws++
                player1.coins += drawCoins
                game.player2?.let {
                    p2RatingChange = ratingService.calculateDrawChange(false)
                    it.draws++; it.coins += Constants.DRAW_COINS; it.totalGames++
                    it.rating = maxOf(0, it.rating + p2RatingChange)
                    userRepository.save(it)
                }
            }
        }

        player1.totalGames++
        player1.rating = maxOf(0, player1.rating + p1RatingChange)
        userRepository.save(player1)

        return GameFinishResult(
            result = result,
            ratingChange = p1RatingChange,
            winnerId = winnerId,
            player1Id = player1.id,
            player1RatingChange = p1RatingChange,
            player2RatingChange = p2RatingChange,
            coinsEarned = coinsEarned
        )
    }

    fun getGameHistory(userId: Long, page: Int, limit: Int): Pair<List<GameResponse>, Long> {
        val pageable = PageRequest.of(page - 1, limit)
        val games = gameRepository.findByUserId(userId, pageable)
        val total = gameRepository.countByUserId(userId)
        return games.map { GameResponse.from(it) } to total
    }

    @Transactional(readOnly = true)
    fun getGame(gameId: Int): GameResponse {
        val game = gameRepository.findById(gameId).orElseThrow { RuntimeException("Game not found") }
        return GameResponse.from(game)
    }

    @Transactional(readOnly = true)
    fun getNextRoundStartData(gameId: Int, playerId: Long): RoundStartData? {
        val round = gameRoundRepository.findNextRound(gameId, playerId).firstOrNull() ?: return null
        return RoundStartData(
            roundId = round.id,
            playerId = round.player.id,
            word = if (round.direction == "en_to_ru") round.word.english else round.word.russian,
            direction = round.direction,
            options = round.options
        )
    }

    @Transactional(readOnly = true)
    fun generateBotQuestion(gameId: Int): BotQuestion {
        val game = gameRepository.findById(gameId).orElseThrow { RuntimeException("Game not found") }
        val level = Level.fromRating(game.player1.rating).name
        val words = wordService.getRandomWordsForLevel(level, 1)
        val word = words.firstOrNull() ?: throw RuntimeException("No words available")
        val direction = if (Random.nextBoolean()) "en_to_ru" else "ru_to_en"
        val correctAnswer = if (direction == "en_to_ru") word.russian else word.english
        val wrongOptions = wordService.getRandomWrongOptions(word, level, 3, direction)
        val allOptions = (listOf(correctAnswer) + wrongOptions).shuffled()
        return BotQuestion(
            word = if (direction == "en_to_ru") word.english else word.russian,
            direction = direction,
            options = allOptions,
            correctAnswer = correctAnswer
        )
    }

    @Transactional(readOnly = true)
    fun getGameInfo(gameId: Int): GameInfo? {
        val game = gameRepository.findById(gameId).orElse(null) ?: return null
        return GameInfo(
            isBotGame = game.isBotGame,
            player1Id = game.player1.id,
            player2Id = game.player2?.id,
            currentTurnId = game.currentTurn
        )
    }
}

data class RoundStartData(
    val roundId: Int,
    val playerId: Long,
    val word: String,
    val direction: String,
    val options: String,
    val timeLimit: Int = 6000
)

data class GameInfo(
    val isBotGame: Boolean,
    val player1Id: Long,
    val player2Id: Long?,
    val currentTurnId: Long?
)

data class AnswerResult(
    val isCorrect: Boolean,
    val correctAnswer: String,
    val currentScore: Pair<Int, Int>,
    val gameFinished: GameFinishResult?,
    val nextTurnPlayerId: Long?
)

data class BotTurnResult(
    val isCorrect: Boolean,
    val currentScore: Pair<Int, Int>,
    val gameFinished: GameFinishResult?
)

data class BotQuestion(
    val word: String,
    val direction: String,
    val options: List<String>,
    val correctAnswer: String
)

data class GameFinishResult(
    val result: String,        // P1's perspective: "win" / "loss" / "tie"
    val ratingChange: Int,     // P1's rating change (kept for compat)
    val winnerId: Long?,       // null = tie
    val player1Id: Long,
    val player1RatingChange: Int,
    val player2RatingChange: Int,
    val coinsEarned: Int       // coins awarded to P1 (for bot games, only P1 matters)
)

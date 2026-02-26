package com.engvie.controller

import com.engvie.model.dto.request.ComputerGameRequest
import com.engvie.model.dto.response.GameResponse
import com.engvie.service.GameService
import com.engvie.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/games")
class GameController(
    private val gameService: GameService,
    private val userService: UserService
) {
    @GetMapping("/history")
    fun getHistory(
        auth: Authentication,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") limit: Int
    ): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val (games, total) = gameService.getGameHistory(userId, page, limit)
        return ResponseEntity.ok(mapOf("games" to games, "total" to total))
    }

    @GetMapping("/{gameId}")
    fun getGame(@PathVariable gameId: Int): ResponseEntity<GameResponse> {
        return ResponseEntity.ok(gameService.getGame(gameId))
    }

    @PostMapping("/computer")
    fun startComputerGame(
        auth: Authentication,
        @RequestBody request: ComputerGameRequest
    ): ResponseEntity<Map<String, Any>> {
        val userId = auth.principal as Long
        val user = userService.getUserById(userId)
        val game = gameService.createComputerGame(user, request.difficulty, request.roundsCount)
        return ResponseEntity.ok(mapOf("gameId" to game.id))
    }
}

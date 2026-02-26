package com.engvie.service

import com.engvie.model.dto.request.AuthRequest
import com.engvie.model.dto.response.AuthResponse
import com.engvie.model.dto.response.UserResponse
import com.engvie.model.entity.User
import com.engvie.repository.UserRepository
import com.engvie.util.JwtUtil
import com.engvie.util.TelegramAuthValidator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val telegramAuthValidator: TelegramAuthValidator,
    private val userService: UserService
) {
    @Transactional
    fun authenticate(request: AuthRequest): AuthResponse {
        val telegramUser = telegramAuthValidator.validate(request.initData)
            ?: throw IllegalArgumentException("Invalid Telegram initData")

        val user = userRepository.findById(telegramUser.id).orElseGet {
            userRepository.save(
                User(
                    id = telegramUser.id,
                    username = telegramUser.username ?: "user${telegramUser.id}",
                    firstName = telegramUser.firstName,
                    lastName = telegramUser.lastName,
                    languageCode = telegramUser.languageCode ?: "ru"
                )
            )
        }.also { u ->
            // Update user info
            u.username = telegramUser.username ?: u.username
            u.firstName = telegramUser.firstName
            u.lastName = telegramUser.lastName
            u.updatedAt = LocalDateTime.now()
            userService.handleDailyLogin(u)
            userRepository.save(u)
        }

        val token = jwtUtil.generateToken(user.id, user.username)
        return AuthResponse(token = token, user = UserResponse.from(user))
    }
}

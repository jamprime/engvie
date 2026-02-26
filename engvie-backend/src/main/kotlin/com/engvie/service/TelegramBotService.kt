package com.engvie.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class TelegramBotService(
    @Value("\${telegram.bot-token:}") private val botToken: String,
    @Value("\${app.mini-app-url:}") private val miniAppUrl: String,
    private val objectMapper: ObjectMapper
) {
    private val log = LoggerFactory.getLogger(TelegramBotService::class.java)
    private val restTemplate = RestTemplate()
    private var lastUpdateId = 0L

    @Scheduled(fixedDelay = 1000)
    fun pollUpdates() {
        if (botToken.isBlank()) return
        try {
            val url = "https://api.telegram.org/bot$botToken/getUpdates?offset=${lastUpdateId + 1}&timeout=0"
            @Suppress("UNCHECKED_CAST")
            val response = restTemplate.getForObject(url, Map::class.java) ?: return
            @Suppress("UNCHECKED_CAST")
            val updates = response["result"] as? List<Map<String, Any>> ?: return
            for (update in updates) {
                val updateId = (update["update_id"] as? Number)?.toLong() ?: continue
                if (updateId > lastUpdateId) lastUpdateId = updateId
                handleUpdate(update)
            }
        } catch (e: Exception) {
            log.debug("Telegram poll error: {}", e.message)
        }
    }

    private fun handleUpdate(update: Map<String, Any>) {
        @Suppress("UNCHECKED_CAST")
        val message = update["message"] as? Map<String, Any> ?: return
        @Suppress("UNCHECKED_CAST")
        val chat = message["chat"] as? Map<String, Any> ?: return
        val chatId = (chat["id"] as? Number)?.toLong() ?: return
        val text = message["text"] as? String ?: return
        if (text.startsWith("/start")) sendWelcome(chatId)
    }

    fun sendEnergyNotification(chatId: Long, energy: Int, maxEnergy: Int) {
        if (botToken.isBlank()) return
        val text = if (energy >= maxEnergy)
            "⚡ Энергия полностью восстановлена! ($energy/$maxEnergy)\n\nГотов к новым битвам? Врывайся!"
        else
            "⚡ +1 энергия восстановлена! ($energy/$maxEnergy)\n\nСледующая зарядится через 30 минут."

        val button = when {
            miniAppUrl.startsWith("https://") -> mapOf("text" to "🎮 Играть", "web_app" to mapOf("url" to miniAppUrl))
            miniAppUrl.isNotBlank() -> mapOf("text" to "🎮 Играть", "url" to miniAppUrl)
            else -> null
        }

        val body = mutableMapOf<String, Any>("chat_id" to chatId, "text" to text)
        if (button != null) body["reply_markup"] = mapOf("inline_keyboard" to listOf(listOf(button)))

        try {
            val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
            val entity = HttpEntity(objectMapper.writeValueAsString(body), headers)
            restTemplate.postForObject("https://api.telegram.org/bot$botToken/sendMessage", entity, Map::class.java)
        } catch (e: Exception) {
            log.debug("Energy notification failed for chatId={}: {}", chatId, e.message)
        }
    }

    private fun sendWelcome(chatId: Long) {
        val button = when {
            miniAppUrl.startsWith("https://") -> mapOf("text" to "🎮 Play Engvie", "web_app" to mapOf("url" to miniAppUrl))
            miniAppUrl.isNotBlank() -> mapOf("text" to "🎮 Play Engvie", "url" to miniAppUrl)
            else -> mapOf("text" to "🎮 Play Engvie", "url" to "https://t.me/")
        }

        val body = mapOf(
            "chat_id" to chatId,
            "text" to "👋 Добро пожаловать в *Engvie*!\n\n⚔️ Сражайся с игроками в дуэлях на знание английских слов\n📚 Учи новые слова и следи за прогрессом\n🏆 Борись за место в таблице лидеров\n\nНажми кнопку ниже, чтобы начать игру!",
            "parse_mode" to "Markdown",
            "reply_markup" to mapOf(
                "inline_keyboard" to listOf(listOf(button))
            )
        )

        try {
            val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
            val entity = HttpEntity(objectMapper.writeValueAsString(body), headers)
            restTemplate.postForObject(
                "https://api.telegram.org/bot$botToken/sendMessage",
                entity, Map::class.java
            )
        } catch (e: Exception) {
            log.error("Failed to send welcome to chatId={}: {}", chatId, e.message)
        }
    }
}

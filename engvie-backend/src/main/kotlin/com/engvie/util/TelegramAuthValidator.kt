package com.engvie.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
class TelegramAuthValidator(
    @Value("\${telegram.bot-token}") private val botToken: String,
    @Value("\${telegram.mock-auth:false}") private val mockAuth: Boolean
) {
    data class TelegramUser(
        val id: Long,
        val firstName: String,
        val lastName: String?,
        val username: String?,
        val languageCode: String?
    )

    fun validate(initData: String): TelegramUser? {
        // Dev mock: accept any initData starting with "mock" or "dev:"
        if (mockAuth) {
            return if (initData.startsWith("dev:")) {
                val parts = initData.split(":")
                TelegramUser(
                    id = parts.getOrNull(1)?.toLongOrNull() ?: 1L,
                    firstName = parts.getOrNull(2) ?: "Dev",
                    lastName = null,
                    username = parts.getOrNull(2) ?: "devuser",
                    languageCode = "ru"
                )
            } else {
                // "mock_init_data" → default test user
                TelegramUser(id = 1L, firstName = "Test", lastName = null, username = "testuser", languageCode = "ru")
            }
        }

        return try {
            val params = parseInitData(initData)
            val hash = params["hash"] ?: return null
            val dataCheckString = buildDataCheckString(params)

            if (verifyHash(dataCheckString, hash)) {
                parseUser(params["user"] ?: return null)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseInitData(initData: String): Map<String, String> {
        return initData.split("&")
            .map { it.split("=", limit = 2) }
            .filter { it.size == 2 }
            .associate {
                URLDecoder.decode(it[0], StandardCharsets.UTF_8) to
                    URLDecoder.decode(it[1], StandardCharsets.UTF_8)
            }
    }

    private fun buildDataCheckString(params: Map<String, String>): String {
        return params.entries
            .filter { it.key != "hash" }
            .sortedBy { it.key }
            .joinToString("\n") { "${it.key}=${it.value}" }
    }

    private fun verifyHash(dataCheckString: String, hash: String): Boolean {
        val secretKey = hmacSha256("WebAppData".toByteArray(), botToken.toByteArray())
        val computedHash = hmacSha256(secretKey, dataCheckString.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return computedHash == hash
    }

    private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }

    private fun parseUser(userJson: String): TelegramUser {
        // Simple JSON parsing
        val id = extractJsonLong(userJson, "id")
        val firstName = extractJsonString(userJson, "first_name") ?: ""
        val lastName = extractJsonString(userJson, "last_name")
        val username = extractJsonString(userJson, "username")
        val languageCode = extractJsonString(userJson, "language_code")

        return TelegramUser(
            id = id,
            firstName = firstName,
            lastName = lastName,
            username = username ?: "user$id",
            languageCode = languageCode ?: "ru"
        )
    }

    private fun extractJsonLong(json: String, key: String): Long {
        val regex = """"$key"\s*:\s*(\d+)""".toRegex()
        return regex.find(json)?.groupValues?.get(1)?.toLong() ?: 0L
    }

    private fun extractJsonString(json: String, key: String): String? {
        val regex = """"$key"\s*:\s*"([^"]*)"""""".toRegex()
        return regex.find(json)?.groupValues?.get(1)
    }
}

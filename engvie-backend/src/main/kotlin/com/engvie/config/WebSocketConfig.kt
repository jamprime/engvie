package com.engvie.config

import com.engvie.util.JwtUtil
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(private val jwtUtil: JwtUtil) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic", "/queue")
        registry.setApplicationDestinationPrefixes("/app")
        registry.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws/game")
            .setAllowedOriginPatterns("*")
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
                if (accessor != null && StompCommand.CONNECT == accessor.command) {
                    val authHeader = accessor.getFirstNativeHeader("Authorization")
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        val token = authHeader.substring(7)
                        try {
                            if (jwtUtil.isTokenValid(token)) {
                                val userId = jwtUtil.getUserIdFromToken(token)
                                val principal = UsernamePasswordAuthenticationToken(
                                    userId.toString(), null,
                                    listOf(SimpleGrantedAuthority("ROLE_USER"))
                                )
                                accessor.user = principal
                            }
                        } catch (e: Exception) {
                            // Invalid token — сессия будет без аутентификации
                        }
                    }
                }
                return message
            }
        })
    }
}

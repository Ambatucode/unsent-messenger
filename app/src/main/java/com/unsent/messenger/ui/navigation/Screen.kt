package com.unsent.messenger.ui.navigation

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String) {
    object Conversations : Screen("conversations")
    object Permissions : Screen("permissions")
    object Settings : Screen("settings")

    object ChatDetail : Screen("chat_detail/{conversationId}/{title}") {
        fun createRoute(conversationId: String, title: String): String {
            val encodedId = URLEncoder.encode(conversationId, StandardCharsets.UTF_8.toString())
            val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
            return "chat_detail/$encodedId/$encodedTitle"
        }

        fun decodeParam(param: String): String {
            return try {
                URLDecoder.decode(param, StandardCharsets.UTF_8.toString())
            } catch (e: Exception) {
                param
            }
        }
    }
}

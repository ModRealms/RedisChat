package net.modrealms.redischat.server.data

data class ChatMessage(
    val serverName: String,
    val username: String,
    val content: String,
    val timestamp: Long
)
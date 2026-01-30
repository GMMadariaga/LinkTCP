package com.gmadariaga.linktcp.domain.model

data class Message(
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val direction: MessageDirection,
    val sizeBytes: Int = content.toByteArray().size
)

enum class MessageDirection {
    SENT,
    RECEIVED
}

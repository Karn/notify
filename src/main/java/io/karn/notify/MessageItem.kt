package io.karn.notify

data class MessageItem(
        val message: CharSequence,
        val timestamp: Long,
        val sender: CharSequence
)

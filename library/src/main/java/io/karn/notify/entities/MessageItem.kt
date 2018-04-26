package io.karn.notify.entities

data class MessageItem(
        val message: CharSequence,
        val timestamp: Long,
        val sender: CharSequence
)

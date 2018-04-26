package io.karn.notify.entities

data class NotifyConfig(
        val defaultChannelName: String = "Application notifications.",
        val defaultChannelKey: String = "application_notification",
        val defaultChannelDescription: String = "Standard application notifications",
        val header: Payload.Header = Payload.Header(channel = defaultChannelKey)
)

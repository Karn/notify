package io.karn.notify.entities

import io.karn.notify.Notify

data class NotifyConfig(
        val defaultChannelKey: String = Notify.DEFAULT_CHANNEL_KEY,
        val defaultChannelName: String = Notify.DEFAULT_CHANNEL_NAME,
        val defaultChannelDescription: String = Notify.DEFAULT_CHANNEL_DESCRIPTION,
        val header: Payload.Header = Payload.Header(channel = defaultChannelKey)
)

package io.karn.notify.entities

internal data class RawNotification(
        internal val meta: Payload.Meta,
        internal val header: Payload.Header,
        internal val content: Payload.Content,
        internal val stackable: Payload.Stackable?
)

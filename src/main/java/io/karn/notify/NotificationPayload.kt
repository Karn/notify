package io.karn.notify

internal data class NotificationPayload(
        internal val meta: Notify.Meta,
        internal val header: Notify.Header,
        internal val content: Notify.Content,
        internal val stackable: Notify.Stackable?
        // private val actions: actions
)

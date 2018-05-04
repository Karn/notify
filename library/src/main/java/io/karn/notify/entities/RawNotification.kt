package io.karn.notify.entities

import io.karn.notify.utils.Action

internal data class RawNotification(
        internal val meta: Payload.Meta,
        internal val header: Payload.Header,
        internal val content: Payload.Content,
        internal val stackable: Payload.Stackable?,
        internal val actions: ArrayList<Action>?
)

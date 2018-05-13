package io.karn.notify.internal

import io.karn.notify.entities.Payload
import io.karn.notify.internal.utils.Action

internal data class RawNotification(
        internal val meta: Payload.Meta,
        internal val alerting: Payload.Alerts,
        internal val header: Payload.Header,
        internal val content: Payload.Content,
        internal val stackable: Payload.Stackable?,
        internal val actions: ArrayList<Action>?
)

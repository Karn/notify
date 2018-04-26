package io.karn.notify

import android.content.Context
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.entities.RawNotification

class Notify internal constructor(private var context: Context) {

    companion object {
        internal var defaultConfig = NotifyConfig()

        fun defaultConfig(block: (NotifyConfig) -> Unit) {
            block(defaultConfig)
        }

        fun with(context: Context): Creator {
            return Creator(Notify(context), defaultConfig)
        }
    }

    init {
        this.context = context.applicationContext

        NotificationInterlop.registerChannel(this.context)
    }

    // Terminal
    internal fun send(payload: RawNotification) {
        val n = NotificationInterlop.buildNotification(context, payload)
        NotificationInterlop.showNotification(context, n)
    }
}

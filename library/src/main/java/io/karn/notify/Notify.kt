package io.karn.notify

import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import io.karn.notify.entities.NotifyConfig
import io.karn.notify.entities.RawNotification

class Notify internal constructor(internal var context: Context) {

    companion object {
        private var defaultConfig = NotifyConfig()

        fun defaultConfig(block: (NotifyConfig) -> Unit) {
            block(defaultConfig)
        }

        fun with(context: Context): Creator {
            return Creator(Notify(context), defaultConfig)
        }
    }

    init {
        this.context = context.applicationContext

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationInterlop.registerChannel(
                    this.context,
                    defaultConfig.defaultChannelKey,
                    defaultConfig.defaultChannelName,
                    defaultConfig.defaultChannelDescription)
        }
    }

    internal fun getBuilder(payload: RawNotification): NotificationCompat.Builder {
        return NotificationInterlop.buildNotification(this, payload)
    }

    // Terminal
    internal fun send(builder: NotificationCompat.Builder): Int {
        return NotificationInterlop.showNotification(context, builder)
    }
}

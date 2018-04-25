package io.karn.notify

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.NotificationCompat

class Notify private constructor(private var context: Context, private var config: NotifyConfig) {

    companion object {
        enum class Types {
            DEFAULT,
            BIG_TEXT,
            BIG_PICTURE,
            PROGRESS,
            MEDIA,
            MESSAGE
        }

        private var config = NotifyConfig()

        fun with(config: NotifyConfig): Companion {
            Companion.config = config
            return Notify
        }

        fun and(context: Context): Creator {
            return with(context)
        }

        fun with(context: Context): Creator {
            return Creator(Notify(context, config))
        }
    }

    init {
        this.context = context.applicationContext

        NotificationInterlop.registerChannel(this.context)
    }

    // Terminal
    internal fun send(payload: NotificationPayload) {
        val n = NotificationInterlop.buildNotification(context, payload)
        NotificationInterlop.showNotification(context, n)
    }

    // Terminal
    internal fun schedule(creator: Creator) {

    }

    data class Meta(
            var clickIntent: Intent? = null,
            var cancelOnClick: Boolean = true,
            var category: String = "",
            var priority: Int = NotificationCompat.PRIORITY_DEFAULT
    )

    data class Header(
            @DrawableRes var icon: Int = R.drawable.ic_app_icon,
            var appName: CharSequence? = null,
            @ColorRes var color: Int = R.color.notification_header_color,
            var headerText: CharSequence? = null,
            var timestamp: Long = -1L
    )

    class Stackable(
            var key: String = "",
            var clickIntent: Intent? = null,
            var summaryContent: String? = null,
            var summaryTitle: ((count: Int) -> String)? = null,
            var summaryDescription: ((count: Int) -> String)? = null
    )

    sealed class Content {

        abstract var title: CharSequence?

        data class Default(
                override var title: CharSequence? = null,
                var text: CharSequence? = null,
                var largeIcon: Drawable? = null
        ) : Content()

        data class ListText(
                override var title: CharSequence? = null,
                var lines: List<CharSequence> = ArrayList()
        ) : Content()

        data class BigText(
                override var title: CharSequence? = null,
                var text: CharSequence? = null
        ) : Content()

        data class BigPicture(
                override var title: CharSequence? = null,
                var text: CharSequence? = null,
                var icon: Bitmap? = null,
                var image: Bitmap? = null
        ) : Content()

        data class Progress(
                override var title: CharSequence? = null,
                var amount: Int = 0
        ) : Content()

        data class Media(
                override var title: CharSequence? = null,
                var image: Drawable? = null
        ) : Content()

        data class Message(
                override var title: CharSequence? = null,
                var userDisplayName: CharSequence = "",
                var conversationTitle: CharSequence? = null,
                var messages: List<MessageItem> = ArrayList()
        ) : Content()
    }
}

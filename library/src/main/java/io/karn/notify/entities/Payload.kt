package io.karn.notify.entities

import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.app.NotificationCompat
import io.karn.notify.R

sealed class Payload {

    data class Meta(
            var timestamp: Long = -1L,
            var clickIntent: PendingIntent? = null,
            var clearIntent: PendingIntent? = null,
            var cancelOnClick: Boolean = true,
            var category: String = "",
            var priority: Int = NotificationCompat.PRIORITY_DEFAULT
    )

    data class Header(
            @DrawableRes var icon: Int = R.drawable.ic_app_icon,
            @ColorRes var color: Int = R.color.notification_header_color,
            var headerText: CharSequence? = null,
            var channel: String = ""
    )

    sealed class Content {

        interface Standard {
            var title: CharSequence?
            var text: CharSequence?
        }

        interface Expandable {
            var expandedText: CharSequence?
        }

        data class Default(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                var largeIcon: Drawable? = null
        ) : Content(), Standard

        data class TextList(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                var lines: List<CharSequence> = ArrayList()
        ) : Content(), Standard

        data class BigText(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                override var expandedText: CharSequence? = null,
                // Defaults to text
                var bigText: CharSequence? = null
        ) : Content(), Standard, Expandable

        data class BigPicture(
                override var title: CharSequence? = null,
                override var text: CharSequence? = null,
                override var expandedText: CharSequence? = null,
                var icon: Bitmap? = null,
                var image: Bitmap? = null
        ) : Content(), Standard, Expandable

        data class Message(
                var conversationTitle: CharSequence? = null,
                var userDisplayName: CharSequence = "",
                var messages: List<NotificationCompat.MessagingStyle.Message> = ArrayList()
        ) : Content()
    }

    data class Stackable(
            var key: String = "",
            var clickIntent: PendingIntent? = null,
            var summaryContent: CharSequence? = null,
            var summaryTitle: ((count: Int) -> String)? = null,
            var summaryDescription: ((count: Int) -> String)? = null,
            var stackableActions: ArrayList<Action>? = null
    )
}

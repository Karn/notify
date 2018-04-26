package io.karn.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.Html
import io.karn.notify.entities.Payload
import io.karn.notify.entities.RawNotification

internal object NotificationInterlop {

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerChannel(context: Context, channelKey: String, channelName: String, channelDescription: String, importance: Int = NotificationManager.IMPORTANCE_DEFAULT) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val channel = NotificationChannel(channelKey, channelName, importance)
        channel.description = channelDescription
        // Register the channel with the system
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, notification: NotificationCompat.Builder): Int {
        val key = NotifyExtender.getKey(notification)
        var id = Utils.getRandomInt()

        if (!key.isNullOrEmpty()) {
            id = Utils.simpleHash(key.toString())
            NotificationManagerCompat.from(context).notify(key.toString(), id, notification.build())
        } else {
            NotificationManagerCompat.from(context).notify(id, notification.build())
        }

        return id
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun getActiveNotifications(context: Context): List<NotifyExtender> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return ArrayList()
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.activeNotifications
                .map { NotifyExtender(it) }
                .filter { it.isValid }
    }

    private fun buildStackedNotification(groupedNotifications: List<NotifyExtender>, builder: NotificationCompat.Builder, payload: RawNotification): NotificationCompat.InboxStyle? {
        if (payload.stackable == null) {
            return null
        }

        val lines: ArrayList<CharSequence> = ArrayList()

        groupedNotifications
                // We only want the notifications that are stackable
                .filter {
                    it.stackable
                }
                // and that match the required key id
                .filter {
                    it.stackKey == payload.stackable.key
                }
                // Then we proceed to rebuild the notification.
                .forEach {
                    // Handle case where we already have a stacked notification.
                    if (it.stacked) {

                        it.stackItems?.forEach {
                            lines.add(it.toString())
                        }

                        return@forEach
                    }

                    it.summaryContent?.let { lines.add(it) }
                }

        if (lines.size == 0) return null
        lines.add(payload.stackable.summaryContent.toString())

        val style = NotificationCompat.InboxStyle()
                // .setText(payload.stackable.summaryTitle?.invoke(lines.size))
                // Finally we update the notifications title to be that of the summary.
                .setBigContentTitle(payload.stackable.summaryTitle?.invoke(lines.size))
                .also { style ->
                    // Add all the lines to the summary.
                    lines.forEach { style.addLine(it) }
                }

        // Update the summary for the builder.
        builder.setStyle(style)
                // Sets the first line of the 'collapsed' RawNotification.
                .setContentTitle(payload.stackable.summaryTitle?.invoke(lines.size))
                // Sets the second line of the 'collapsed' RawNotification.
                .setContentText(payload.stackable.summaryDescription?.invoke(lines.size))
                .extend(
                        NotifyExtender().setStacked(true)
                )

        return style
    }

    fun buildNotification(notify: Notify, payload: RawNotification): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(notify.context, payload.header.channel)
                // The color of the RawNotification Icon, App_Name and the expanded chevron.
                .setColor(notify.context.resources.getColor(payload.header.color))
                // The RawNotification icon.
                .setSmallIcon(payload.header.icon)
                .setAutoCancel(payload.meta.cancelOnClick)
                // Set the click handler for the notifications
                .setContentIntent(payload.meta.clickIntent)
                // Set the handler in the event that the notification is dismissed.
                .setDeleteIntent(payload.meta.clearIntent)

        // Standard notifications have the collapsed title and text.
        if (payload.content is Payload.Content.Standard) {
            // This is the title of the RawNotification.
            builder.setContentTitle(payload.content.title)
                    // THis is the text of the 'collapsed' RawNotification.
                    .setContentText(payload.content.text)
        }

        payload.run {
            var style: NotificationCompat.Style?

            if (stackable != null) {
                builder.setContentIntent(stackable.clickIntent)
                        .extend(NotifyExtender()
                                .setKey(stackable.key)
                                .setStackable(true)
                                .setSummaryText(stackable.summaryContent))

                val activeNotifications = getActiveNotifications(notify.context)
                if (activeNotifications.isNotEmpty()) {
                    style = buildStackedNotification(activeNotifications, builder, payload)

                    if (style != null) {
                        return@run
                    }
                }
            }

            style = when (this.content) {
                is Payload.Content.Default -> {
                    // Nothing to do here. There is no expanded text.
                    null
                }
                is Payload.Content.TextList -> {
                    NotificationCompat.InboxStyle().also { style ->
                        content.lines.forEach { style.addLine(it) }
                    }
                }
                is Payload.Content.BigText -> {
                    // Override the behavior of the second line.
                    builder.setContentText(Html.fromHtml("<font color='#3D3D3D'>" + (content.text
                            ?: "")
                            .toString() + "</font>"))

                    val bigText: CharSequence = Html.fromHtml("<font color='#3D3D3D'>" + (content.expandedText
                            ?: content.title
                            ?: "")
                            .toString() + "</font><br>" + content.bigText?.replace("\n".toRegex(), "<br>"))

                    NotificationCompat.BigTextStyle()
                            .bigText(bigText)
                }
                is Payload.Content.BigPicture -> {
                    // Document these by linking to resource with labels. (1), (2), etc.

                    // This large icon is show in both expanded and collapsed views. Might consider creating a custom view for this.
                    // builder.setLargeIcon(content.image)

                    NotificationCompat.BigPictureStyle()
                            // This is the second line in the 'expanded' notification.
                            .setSummaryText(content.expandedText ?: content.text)
                            // This is the picture below.
                            .bigPicture(content.image)
                }
                is Payload.Content.Message -> {
                    NotificationCompat.MessagingStyle(content.userDisplayName)
                            .setConversationTitle(content.conversationTitle)
                            .also { s ->
                                content.messages.forEach { s.addMessage(it.message, it.timestamp, it.sender) }
                            }
                }
            }

            builder.setStyle(style)
        }

        return builder
    }
}

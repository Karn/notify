package io.karn.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.text.Html
import io.karn.notify.entities.Payload
import io.karn.notify.entities.RawNotification
import java.util.concurrent.ThreadLocalRandom

internal object NotificationInterlop {

    private const val IS_STACKABLE = "is_stackable"
    private const val IS_STACKED = "is_stacked"
    private const val STACKABLE_KEY = "stack_key"
    private const val SUMMARY_TEXT = "summary_text"

    private const val BACKGROUND_TASK_NOTIFICATION_CHANNEL_NAME = "Background Task Notifications"
    private const val BACKGROUND_TASK_NOTIFICATION_CHANNEL = "background_task_notification"

    private fun getRandomInt(): Int {
        return ThreadLocalRandom.current().nextInt(0, Int.MAX_VALUE)
    }

    private fun simpleHash(str: String): Int {
        val out = StringBuilder()
        str.toCharArray().map { out.append(it.toByte()) }

        return out.toString().substring(0, 6).toInt()
    }


    fun registerChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val channel = NotificationChannel(BACKGROUND_TASK_NOTIFICATION_CHANNEL, BACKGROUND_TASK_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = "Handle local notifications that result from the background notifications."
        // Register the channel with the system
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showNotification(context: Context, notification: NotificationCompat.Builder): Int {
        val notification = notification.build()
        var id = getRandomInt()
        if (!notification.group.isNullOrEmpty()) {
            id = simpleHash(notification.group)
            NotificationManagerCompat.from(context).notify(notification.group, id, notification)
        } else {
            NotificationManagerCompat.from(context).notify(id, notification)
        }

        return id
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }

    private fun buildStackedNotification(context: Context, builder: NotificationCompat.Builder, payload: RawNotification): NotificationCompat.InboxStyle? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || payload.stackable == null) {
            return null
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Get all the notifications that are relevant.
        val groupedNotifications = notificationManager.activeNotifications
        // Timber.d("Notifications: %s", groupedNotifications.toString())

        val lines: ArrayList<CharSequence> = ArrayList()

        groupedNotifications
                // We only want the notifications that are stackable
                .filter { it.notification.extras.getBoolean(IS_STACKABLE, false) }
                // and that match the required key id
                .filter { it.notification.extras.getCharSequence(STACKABLE_KEY, "") == payload.stackable.key }
                // Then we proceed to rebuild the notification.
                .forEach {
                    // Handle case where we already have a stacked notification.
                    val isStacked = it.notification.extras.getBoolean(IS_STACKED, false)
                    if (isStacked) {
                        // Timber.d("Found stacked notification")
                        it.notification.extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)?.forEach {
                            lines.add(it.toString())
                        }

                        return@forEach
                    }

                    // This means that we are seeing a notification for the first time. And need to add its summary to the
                    val template = it.notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE)
                    // Timber.d("Found notification with template: %s. Adding summary to new notification.", template)
                    lines.add(it.notification.extras.getCharSequence(SUMMARY_TEXT))
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

        builder.extras.putBoolean(IS_STACKED, true)

        // Sets the first line of the 'collapsed' RawNotification.
        builder.setContentTitle(payload.stackable.summaryTitle?.invoke(lines.size))
        // Sets the second line of the 'collapsed' RawNotification.
        builder.setContentText(payload.stackable.summaryDescription?.invoke(lines.size))

        return style
    }

    fun buildNotification(context: Context, payload: RawNotification): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, BACKGROUND_TASK_NOTIFICATION_CHANNEL)
                // The color of the RawNotification Icon, App_Name and the expanded chevron.
                .setColor(context.resources.getColor(payload.header.color))
                // The RawNotification icon.
                .setSmallIcon(payload.header.icon)
                .setAutoCancel(payload.meta.cancelOnClick)

        // Standard notifications have the collapsed title and text.
        if (payload.content is Payload.Content.Standard) {
            // This is the title of the RawNotification.
            builder.setContentTitle(payload.content.title)
                    // THis is the text of the 'collapsed' RawNotification.
                    .setContentText(payload.content.text)
        }

        payload.meta.clickIntent?.let { intent ->
            builder.setContentIntent(intent)
        }

        payload.run {
            var style: NotificationCompat.Style? = null

            payload.stackable?.let {
                style = buildStackedNotification(context, builder, payload)
            }

            if (style != null) {
                return@run
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

        payload.stackable?.let {
            builder.setGroup(payload.stackable.key)
            builder.setContentIntent(PendingIntent.getActivity(context.applicationContext,
                    0,
                    it.clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            ))
            builder.extras.putBoolean(IS_STACKABLE, true)
            builder.extras.putCharSequence(SUMMARY_TEXT, it.summaryContent)
        }

        return builder
    }
}

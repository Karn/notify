package io.karn.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
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

    private fun buildStackedNotification(context: Context, builder: NotificationCompat.Builder, payload: NotificationPayload): NotificationCompat.InboxStyle? {
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
                // .setSummaryText(payload.stackable.summaryTitle?.invoke(lines.size))
                // Finally we update the notifications title to be that of the summary.
                .setBigContentTitle(payload.stackable.summaryTitle?.invoke(lines.size))
                .also { style ->
                    // Add all the lines to the summary.
                    lines.forEach { style.addLine(it) }
                }

        // Update the summary for the builder.
        builder.setStyle(style)

        builder.extras.putBoolean(IS_STACKED, true)
        builder.setContentTitle(payload.stackable.summaryTitle?.invoke(lines.size))
        builder.setContentText(payload.stackable.summaryDescription?.invoke(lines.size))

        return style
    }


    fun buildNotification(context: Context, payload: NotificationPayload): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, BACKGROUND_TASK_NOTIFICATION_CHANNEL)
                .setColor(payload.header.color)
                .setSmallIcon(payload.header.icon)
                .setAutoCancel(payload.meta.cancelOnClick)

        payload.meta.clickIntent?.let { intent ->
            builder.setContentIntent(PendingIntent.getActivity(context.applicationContext,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            ))
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
                is Notify.Content.Default -> {
                    builder.setContentTitle(content.title)
                    builder.setContentText(content.text)

                    null
                }
                is Notify.Content.ListText -> {
                    val style = NotificationCompat.InboxStyle()
                            .setSummaryText(payload.header.headerText)
                            .setBigContentTitle(content.title)

                    content.lines.map { style.addLine(it) }

                    style
                }
                is Notify.Content.BigText -> {
                    NotificationCompat.BigTextStyle()
                            .setSummaryText(payload.header.headerText)
                            .setBigContentTitle(content.title)
                            .bigText(content.text)
                }
                is Notify.Content.BigPicture -> {
                    NotificationCompat.BigPictureStyle()
                            .setSummaryText(payload.header.headerText)
                            .setBigContentTitle(content.title)
                            .bigLargeIcon(content.icon)
                            .bigPicture(content.image)
                }
                is Notify.Content.Progress -> {
                    null
                }
                is Notify.Content.Media -> {
                    null
                }
                is Notify.Content.Message -> {
                    val style = NotificationCompat.MessagingStyle(content.userDisplayName)
                            .setConversationTitle(content.conversationTitle)

                    content.messages.forEach { style.addMessage(it.message, it.timestamp, it.sender) }

                    style
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

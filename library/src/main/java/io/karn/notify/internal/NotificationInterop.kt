/*
 * MIT License
 *
 * Copyright (c) 2018 Karn Saheb
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.karn.notify.internal

import android.app.NotificationManager
import android.os.Build
import android.text.Html
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat
import io.karn.notify.Notify
import io.karn.notify.entities.Payload
import io.karn.notify.internal.utils.Utils

internal object NotificationInterop {

    fun showNotification(notificationManager: NotificationManager, id: Int?, notification: NotificationCompat.Builder): Int {
        val key = NotifyExtender.getKey(notification.extras)
        var id = id ?: Utils.getRandomInt()

        if (key != null) {
            id = key.hashCode()
            notificationManager.notify(key.toString(), id, notification.build())
        } else {
            notificationManager.notify(id, notification.build())
        }

        return id
    }

    fun cancelNotification(notificationManager: NotificationManager, notificationId: Int) {
        notificationManager.cancel(notificationId)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getActiveNotifications(notificationManager: NotificationManager): List<NotifyExtender> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return ArrayList()
        }

        return notificationManager.activeNotifications
                .map { NotifyExtender(it) }
                .filter { it.valid }
    }

    private fun buildStackedNotification(groupedNotifications: List<NotifyExtender>, builder: NotificationCompat.Builder, payload: RawNotification): NotificationCompat.InboxStyle? {
        if (payload.stackable == null) {
            return null
        }

        val lines: ArrayList<CharSequence> = ArrayList()

        groupedNotifications
                // We only want the notifications that are stackable
                .filter { it.stackable }
                // and that match the required key id
                .filter { it.stackKey == payload.stackable.key }
                // Then we proceed to rebuild the notification.
                .forEach {
                    // Handle case where we already have a stacked notification.
                    if (it.stacked) {
                        it.stackItems?.forEach { lines.add(it.toString()) }
                    } else {
                        it.summaryContent?.let { lines.add(it) }
                    }
                }

        if (lines.size == 0) return null
        lines.add(payload.stackable.summaryContent.toString())

        val style = NotificationCompat.InboxStyle()
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
                .setContentText(Utils.getAsSecondaryFormattedText(payload.stackable.summaryDescription?.invoke(lines.size)))
                // Attach the stack click handler.
                .setContentIntent(payload.stackable.clickIntent)
                .extend(NotifyExtender().setStacked(true))

        // Clear the current set of actions and re-apply the stackable actions.
        builder.mActions.clear()
        payload.stackable.stackableActions?.forEach {
            builder.addAction(it)
        }

        return style
    }

    fun buildNotification(notify: Notify, payload: RawNotification): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(notify.context, payload.alerting.channelKey)
                // Ensures that this notification is marked as a Notify notification.
                .extend(NotifyExtender())
                // The color of the RawNotification Icon, App_Name and the expanded chevron.
                .setColor(payload.header.color)
                // The RawNotification icon.
                .setSmallIcon(payload.header.icon)
                // The text that is visible to the right of the app name in the notification header.
                .setSubText(payload.header.headerText)
                // Show the relative timestamp next to the application name.
                .setShowWhen(payload.header.showTimestamp)
                // Dismiss the notification on click?
                .setAutoCancel(payload.meta.cancelOnClick)
                // Set the click handler for the notifications
                .setContentIntent(payload.meta.clickIntent)
                // Set the handler in the event that the notification is dismissed.
                .setDeleteIntent(payload.meta.clearIntent)
                // The category of the notification which allows android to prioritize the
                // notification as required.
                .setCategory(payload.meta.category)
                // Set the key by which this notification will be grouped.
                .setGroup(payload.meta.group)
                // Set whether or not this notification is only relevant to the current device.
                .setLocalOnly(payload.meta.localOnly)
                // Set whether this notification is sticky.
                .setOngoing(payload.meta.sticky)
                // The duration of time after which the notification is automatically dismissed.
                .setTimeoutAfter(payload.meta.timeout)

        if (payload.progress.showProgress) {
            if (payload.progress.enablePercentage) builder.setProgress(100,payload.progress.progressPercent,false)
            else builder.setProgress(0,0,true)
        }

        // Add contacts if any -- will help display prominently if possible.
        payload.meta.contacts.takeIf { it.isNotEmpty() }?.forEach {
            builder.addPerson(it)
        }

        // Standard notifications have the collapsed title and text.
        if (payload.content is Payload.Content.Standard) {
            // This is the title of the RawNotification.
            builder.setContentTitle(payload.content.title)
                    // THis is the text of the 'collapsed' RawNotification.
                    .setContentText(payload.content.text)
        }

        if (payload.content is Payload.Content.SupportsLargeIcon) {
            // Sets the large icon of the notification.
            builder.setLargeIcon(payload.content.largeIcon)
        }

        // Attach all the actions.
        payload.actions?.forEach {
            builder.addAction(it)
        }

        // Attach alerting options.
        payload.alerting.apply {
            // Register the default alerting. Applies channel configuration on API >= 26.
            NotificationChannelInterop.with(this)

            // The visibility of the notification on the lockscreen.
            builder.setVisibility(lockScreenVisibility)

            // The lights of the notification.
            if (lightColor != Notify.NO_LIGHTS) {
                builder.setLights(lightColor, 500, 2000)
            }

            // Manual specification of the priority. According to the documentation, this is only
            // one of the factors that affect the notifications priority and that this behaviour may
            // differ on different platforms.
            // It seems that the priority is also affected by the sound that is set for the
            // notification as such we'll wrap the behaviour of the sound and also of the vibration
            // to prevent the notification from being reclassified to a different priority.
            // This doesn't seem to be the case for API >= 26, however, a future PR should tackle
            // API nuances and ensure that behaviour has been tested.
            // TODO: Test API nuances.
            builder.priority = channelImportance

            // If the notification's importance is normal or greater then we configure
            if (channelImportance >= Notify.IMPORTANCE_NORMAL) {
                // The vibration pattern.
                vibrationPattern
                        .takeIf { it.isNotEmpty() }
                        ?.also {
                            builder.setVibrate(it.toLongArray())
                        }

                // A custom alerting sound.
                builder.setSound(sound)
            }
        }

        payload.bubblize
                ?.takeIf { Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q }
                ?.also {
                    builder.bubbleMetadata = NotificationCompat.BubbleMetadata.Builder()
                            .setDesiredHeight(it.desiredHeight)
                            .setIntent(it.targetActivity!!)
                            .setIcon(it.bubbleIcon!!)
                            .setAutoExpandBubble(it.autoExpand)
                            .setSuppressNotification(it.suppressInitialNotification)
                            .setDeleteIntent(it.clearIntent)
                            .build()
                }

        var style: NotificationCompat.Style? = null

        payload.stackable?.let {
            builder.extend(NotifyExtender()
                    .setKey(it.key)
                    .setStackable(true)
                    .setSummaryText(it.summaryContent))

            val activeNotifications = getActiveNotifications(Notify.defaultConfig.notificationManager!!)
            if (activeNotifications.isNotEmpty()) {
                style = buildStackedNotification(activeNotifications, builder, payload)
            }
        }

        if (style == null) {
            style = setStyle(builder, payload.content)
        }

        builder.setStyle(style)

        return builder
    }

    private fun setStyle(builder: NotificationCompat.Builder, content: Payload.Content): NotificationCompat.Style? {
        return when (content) {
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
                builder.setContentText(Utils.getAsSecondaryFormattedText((content.text
                        ?: "").toString()))

                val formattedExpandedText = content.expandedText?.let {
                    "<font color='#3D3D3D'>$it</font><br>"
                } ?: ""

                val bigText: CharSequence = Html.fromHtml(formattedExpandedText + content.bigText?.replace("\n".toRegex(), "<br>"))

                NotificationCompat.BigTextStyle()
                        .bigText(bigText)
            }
            is Payload.Content.BigPicture -> {
                NotificationCompat.BigPictureStyle()
                        // This is the second line in the 'expanded' notification.
                        .setSummaryText(content.expandedText ?: content.text)
                        // This is the picture below.
                        .bigPicture(content.image)
                        .bigLargeIcon(null)

            }
            is Payload.Content.Message -> {
                NotificationCompat.MessagingStyle(content.userDisplayName)
                        .setConversationTitle(content.conversationTitle)
                        .also { s ->
                            content.messages.forEach { s.addMessage(it.text, it.timestamp, it.sender) }
                        }
            }
        }
    }
}

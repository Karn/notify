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

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.os.Build
import io.karn.notify.Notify
import io.karn.notify.entities.Payload

/**
 * Provides compatibility functionality for the Notification channels introduced in Android O.
 */
internal object NotificationChannelInterop {
    @SuppressLint("WrongConstant")
    fun with(alerting: Payload.Alerts): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }

        val notificationManager = Notify.defaultConfig.notificationManager!!

        // Ensure that the alerting is not already registered -- return true if it exists.
        notificationManager.getNotificationChannel(alerting.channelKey)?.run {
            return true
        }

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val channel = NotificationChannel(alerting.channelKey, alerting.channelName, alerting.channelImportance + 3).apply {
            description = alerting.channelDescription

            // Set the lockscreen visibility.
            lockscreenVisibility = alerting.lockScreenVisibility

            alerting.lightColor
                    .takeIf { it != Notify.NO_LIGHTS }
                    ?.let {
                        enableLights(true)
                        lightColor = alerting.lightColor
                    }

            alerting.vibrationPattern.takeIf { it.isNotEmpty() }?.also {
                enableVibration(true)
                vibrationPattern = it.toLongArray()
            }

            alerting.sound.also {
                setSound(it, android.media.AudioAttributes.Builder().build())
            }

            Unit
        }

        // Register the alerting with the system
        notificationManager.createNotificationChannel(channel)

        return true
    }
}

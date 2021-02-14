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

package io.karn.notify.entities

import android.app.NotificationManager

/**
 * Provider of the initial configuration of the Notify > NotifyCreator Fluent API.
 */
data class NotifyConfig(
        /**
         * A reference to the notification manager.
         */
        internal var notificationManager: NotificationManager? = null,
        /**
         * Specifies the default configuration of a notification (e.g the default notificationIcon,
         * and notification color.)
         */
        internal var defaultHeader: Payload.Header = Payload.Header(),
        /**
         * Specifies the default configuration of a progress (e.g the default progress type)
         */
        internal var defaultProgress: Payload.Progress = Payload.Progress(),
        /**
         * Specifies the default alerting configuration for notifications.
         */
        internal var defaultAlerting: Payload.Alerts = Payload.Alerts()
) {
    fun header(init: Payload.Header.() -> Unit): NotifyConfig {
        defaultHeader.init()
        return this
    }

    fun alerting(key: String, init: Payload.Alerts.() -> Unit): NotifyConfig {
        // Clone object and assign the key.
        defaultAlerting = defaultAlerting.copy(channelKey = key)
        defaultAlerting.init()
        return this
    }

    fun progress(init: Payload.Progress.() -> Unit): NotifyConfig {
        defaultProgress.init()
        return this
    }
}

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

import android.os.Bundle
import android.service.notification.StatusBarNotification
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat

/**
 * Helper class to add Notify Extensions to a notification. The extensions contain data specific to
 * notifications created by the Notify class, these extensions include data on functionality such as
 * forced stacking.
 *
 * Notify Extensions can be accessed on an existing notification by using the
 * {@code NotifyExtender(Notification)} constructor, and then using property access to get the
 * values.
 */
internal class NotifyExtender : NotificationCompat.Extender {

    internal companion object {
        /**
         * Identifies the bundle that is associated
         */
        private const val EXTRA_NOTIFY_EXTENSIONS = "io.karn.notify.EXTENSIONS"

        // Used to determine if an instance of this class is a valid Notify Notification object.
        private const val VALID = "notify_valid"

        // Keys within EXTRA_NOTIFY_EXTENSIONS for synthetic notification options.
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val STACKABLE = "stackable"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val STACKED = "stacked"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val STACK_KEY = "stack_key"
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal const val SUMMARY_CONTENT = "summary_content"

        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        internal fun getExtensions(extras: Bundle): Bundle {
            return extras.getBundle(EXTRA_NOTIFY_EXTENSIONS) ?: Bundle()
        }

        internal fun getKey(extras: Bundle): CharSequence? {
            return getExtensions(extras).getCharSequence(STACK_KEY, null)
        }
    }

    var valid: Boolean = false
        internal set(value) {
            field = value
        }

    var stackable: Boolean = false
        internal set(value) {
            field = value
        }
    var stacked: Boolean = false
        internal set(value) {
            field = value
        }
    var stackKey: CharSequence? = null
        internal set(value) {
            field = value
        }
    var stackItems: ArrayList<CharSequence>? = null
        internal set(value) {
            field = value
        }

    var summaryContent: CharSequence? = null
        internal set(value) {
            field = value
        }

    constructor() {
        this.valid = true
    }

    /**
     * Build a Notify notification from an existing notification.
     */
    constructor(notification: StatusBarNotification) {
        // Fetch the extensions if any, from a given notification.
        NotificationCompat.getExtras(notification.notification)?.let { bundle ->
            bundle.getBundle(EXTRA_NOTIFY_EXTENSIONS)?.let {
                loadConfigurationFromBundle(it)
            }
            bundle.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)?.let {
                stackItems = ArrayList(it.toList())
            }
        }
    }

    override fun extend(builder: NotificationCompat.Builder): NotificationCompat.Builder {
        val notifyExtensions = builder.extras.getBundle(EXTRA_NOTIFY_EXTENSIONS) ?: Bundle()
        loadConfigurationFromBundle(notifyExtensions)

        notifyExtensions.putBoolean(VALID, valid)

        if (stackable) {
            notifyExtensions.putBoolean(STACKABLE, stackable)
        }

        if (!stackKey.isNullOrBlank()) {
            notifyExtensions.putCharSequence(STACK_KEY, stackKey)
        }

        if (stacked) {
            notifyExtensions.putBoolean(STACKED, stacked)
        }

        if (!summaryContent.isNullOrBlank()) {
            notifyExtensions.putCharSequence(SUMMARY_CONTENT, summaryContent)
        }

        builder.extras.putBundle(EXTRA_NOTIFY_EXTENSIONS, notifyExtensions)
        return builder
    }

    private fun loadConfigurationFromBundle(bundle: Bundle) {
        // Perform an update if exists on all properties.
        valid = bundle.getBoolean(VALID, valid)

        stackable = bundle.getBoolean(STACKABLE, stackable)
        stacked = bundle.getBoolean(STACKED, stacked)
        stackKey = bundle.getCharSequence(STACK_KEY, stackKey)

        summaryContent = bundle.getCharSequence(SUMMARY_CONTENT, summaryContent)
    }

    internal fun setStackable(stackable: Boolean = true): NotifyExtender {
        this.stackable = stackable
        return this
    }

    internal fun setStacked(stacked: Boolean = true): NotifyExtender {
        this.stacked = stacked
        return this
    }

    internal fun setKey(key: CharSequence?): NotifyExtender {
        this.stackKey = key
        return this
    }

    internal fun setSummaryText(text: CharSequence?): NotifyExtender {
        this.summaryContent = text
        return this
    }
}

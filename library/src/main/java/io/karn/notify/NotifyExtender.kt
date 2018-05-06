package io.karn.notify

import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.support.annotation.VisibleForTesting
import android.support.v4.app.NotificationCompat

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
        NotificationCompat.getExtras(notification.notification)?.getBundle(EXTRA_NOTIFY_EXTENSIONS)?.let {
            loadConfigurationFromBundle(it)
        }
    }

    override fun extend(builder: NotificationCompat.Builder): NotificationCompat.Builder {
        val notifyExtensions = builder.extras.getBundle(EXTRA_NOTIFY_EXTENSIONS) ?: Bundle()
        loadConfigurationFromBundle(notifyExtensions)

        if (valid) {
            notifyExtensions.putBoolean(VALID, valid)
        }

        if (stackable) {
            notifyExtensions.putBoolean(STACKABLE, stackable)

            if (stackKey.isNullOrBlank()) {
                throw IllegalStateException("Specified stackable notification but failed to include stackKey.")
            }

            notifyExtensions.putCharSequence(STACK_KEY, stackKey)
        } else {
            if (stacked) {
                throw IllegalStateException("Specified as stacked but notification is not stackable.")
            }
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
        stackItems = bundle.getCharSequenceArrayList(NotificationCompat.EXTRA_TEXT_LINES)

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

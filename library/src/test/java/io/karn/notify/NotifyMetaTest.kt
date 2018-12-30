package io.karn.notify

import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotifyMetaTest : NotifyTestBase() {

    @Test
    fun defaultMetadataTest() {
        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertNull(notification.contentIntent)
        Assert.assertNull(notification.deleteIntent)
        Assert.assertTrue((notification.flags and NotificationCompat.FLAG_AUTO_CANCEL) != 0)
        Assert.assertNull(notification.category)
    }

    @Test
    fun modifiedMetadataTest() {
        val testClickIntent = PendingIntent.getActivity(this.context, 0, Intent(Settings.ACTION_SYNC_SETTINGS), 0)
        val testClearIntent = PendingIntent.getActivity(this.context, 0, Intent(Settings.ACTION_SETTINGS), 0)

        val testCancelOnClick = false
        val testCategory = NotificationCompat.CATEGORY_STATUS
        val testTimeout = 5000L

        val notification = Notify.with(this.context)
                .meta {
                    clickIntent = testClickIntent
                    clearIntent = testClearIntent
                    cancelOnClick = testCancelOnClick
                    category = testCategory
                    timeout = testTimeout
                    people {
                        add("mailto:hello@test.com")
                    }
                }
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(testClickIntent, notification.contentIntent)
        Assert.assertEquals(testClearIntent, notification.deleteIntent)
        Assert.assertEquals(testCancelOnClick, (notification.flags and NotificationCompat.FLAG_AUTO_CANCEL) != 0)
        Assert.assertEquals(testCategory, notification.category)
        Assert.assertEquals(testTimeout, notification.timeoutAfter)
        Assert.assertEquals(1, notification.extras.getStringArray(NotificationCompat.EXTRA_PEOPLE)?.size
                ?: 0)
    }
}

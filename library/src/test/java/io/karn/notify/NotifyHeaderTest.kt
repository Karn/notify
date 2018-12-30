package io.karn.notify

import androidx.core.app.NotificationCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotifyHeaderTest : NotifyTestBase() {

    @Test
    fun defaultHeaderTest() {
        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(R.drawable.ic_app_icon, notification.smallIcon.resId)
        // Validating color is not reliable. The notification color is randomly returned as ##FFAAAAAA
        Assert.assertEquals(0x4A90E2, 0xFFFFFF and notification.color)
        Assert.assertEquals(null, notification.extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
        Assert.assertTrue(notification.extras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN))
    }

    @Test
    fun modifiedHeaderTest() {
        val testIcon = R.drawable.ic_android_black
        val testColor = 0xAA66CC
        val testHeaderText = "New Menu!"
        val testShowTimestamp = false

        val notification = Notify.with(this.context)
                .header {
                    icon = testIcon
                    color = testColor
                    headerText = testHeaderText
                    showTimestamp = testShowTimestamp
                }
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(testIcon, notification.smallIcon.resId)
        Assert.assertEquals(testColor, 0xFFFFFF and notification.color)
        Assert.assertEquals(testHeaderText, notification.extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
        Assert.assertEquals(testShowTimestamp, notification.extras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN))
    }
}

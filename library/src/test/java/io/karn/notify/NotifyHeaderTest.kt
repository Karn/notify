package io.karn.notify

import android.support.v4.app.NotificationCompat
import junit.framework.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotifyHeaderTest : NotifyTestBase() {

    @Test
    @Ignore
    fun defaultHeaderTest() {

        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(context.resources.getDrawable(R.drawable.ic_app_icon, context.theme), notification.smallIcon.loadDrawable(context))
        // Validating color is not reliable. The notification color is randomly returned as ##FFAAAAAA
        Assert.assertEquals(
                String.format("#%06X", context.resources.getColor(R.color.notification_header_color, context.theme)),
                String.format("#%06X", 0xFFFFFFFF and notification.color.toLong()))
        Assert.assertEquals(null, notification.extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
        Assert.assertEquals(Notify.CHANNEL_DEFAULT_KEY, notification.channelId)
        Assert.assertTrue(notification.extras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN))
    }

    @Test
    fun modifiedHeaderTest() {
        val testIcon = R.drawable.ic_android_black
        val testColor = android.R.color.holo_purple
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

        Assert.assertEquals(context.resources.getDrawable(testIcon, context.theme), notification.smallIcon.loadDrawable(context))
        Assert.assertEquals(
                String.format("#%06X", context.resources.getColor(testColor, context.theme)),
                String.format("#%06X", 0xFFFFFFFF and notification.color.toLong()))
        Assert.assertEquals(testHeaderText, notification.extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
        Assert.assertEquals(testShowTimestamp, notification.extras.getBoolean(NotificationCompat.EXTRA_SHOW_WHEN))
    }
}

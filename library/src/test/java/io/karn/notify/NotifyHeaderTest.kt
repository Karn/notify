package io.karn.notify

import android.app.Application
import android.support.v4.app.NotificationCompat
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NotifyHeaderTest {

    private val context: Application = RuntimeEnvironment.application

    @Test
    fun defaultHeaderTest() {
        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()
        
        Assert.assertEquals(context.resources.getDrawable(R.drawable.ic_app_icon, context.theme), notification.smallIcon.loadDrawable(context))
        Assert.assertEquals(context.resources.getColor(R.color.notification_header_color, context.theme), notification.color)
        Assert.assertEquals(null, notification.extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
        Assert.assertEquals(Notify.DEFAULT_CHANNEL_KEY, notification.channelId)
    }

    @Test
    fun modifiedHeaderTest() {
        val testIcon = R.drawable.ic_android_black
        val testColor = android.R.color.holo_purple
        val testHeaderText = "New Menu!"
        val testChannel = "test_channel"

        val notification = Notify.with(this.context)
                .header {
                    icon = testIcon
                    color = testColor
                    headerText = testHeaderText
                    channel = testChannel
                }
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertEquals(context.resources.getDrawable(testIcon, context.theme), notification.smallIcon.loadDrawable(context))
        Assert.assertEquals(context.resources.getColor(testColor, context.theme), notification.color)
        Assert.assertEquals(testHeaderText, notification.extras.getCharSequence(NotificationCompat.EXTRA_SUB_TEXT))
        Assert.assertEquals(testChannel, notification.channelId)
    }
}

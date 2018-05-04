package io.karn.notify

import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import io.karn.notify.utils.Action
import io.karn.notify.utils.Errors
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NotifyStackableTest {

    private val context: Application = RuntimeEnvironment.application

    @Test
    fun defaultStackableTest() {
        val testTitle = "New dessert menu"
        val testText = "The Cheesecake Factory has a new dessert for you to try!"

        val notification = Notify.with(this.context)
                .content {
                    title = testTitle
                    text = testText
                }
                .asBuilder()
                .build()

        Assert.assertEquals(testTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
        Assert.assertEquals(testText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT))
        Assert.assertEquals(null, NotifyExtender.getKey(notification.extras))
        Assert.assertNull(notification.contentIntent)
        Assert.assertNull(NotifyExtender.getExtensions(notification.extras).getCharSequence(NotifyExtender.SUMMARY_CONTENT))
        Assert.assertNull(notification.actions)
    }

    @Test
    fun invalidStackKeyTest() {
        val testTitle = "New dessert menu"
        val testText = "The Cheesecake Factory has a new dessert for you to try!"

        var exceptionThrown: IllegalArgumentException? = null

        try {
            Notify.with(this.context)
                    .content {
                        title = testTitle
                        text = testText
                    }
                    .stackable {
                        this.summaryContent = "Invalid summary"
                    }
                    .asBuilder()
                    .build()
        } catch (e: IllegalArgumentException) {
            exceptionThrown = e
        }

        Assert.assertNotNull(exceptionThrown)
        Assert.assertEquals(Errors.INVALID_STACK_KEY_ERROR, exceptionThrown?.message)
    }

    @Test
    fun singleStackableTest() {
        val testTitle = "New dessert menu"
        val testText = "The Cheesecake Factory has a new dessert for you to try!"
        val testKey = "test_key"
        val testClickIntent = PendingIntent.getActivity(this.context, 0, Intent(Settings.ACTION_SETTINGS), 0)
        val testSummaryContent = "New desserts available to try!"
        val testSummaryTitle = " new dessert menus"
        val testSummaryText = "Try out these delicious new menu items today!"
        val testActionText = "Action"
        val testActionIntent = PendingIntent.getActivity(this.context, 0, Intent(Settings.ACTION_SYNC_SETTINGS), 0)

        val notification = Notify.with(this.context)
                .content {
                    title = testTitle
                    text = testText
                }
                .stackable {
                    this.key = testKey
                    this.clickIntent = testClickIntent
                    this.summaryContent = testSummaryContent
                    this.summaryTitle = { count -> count.toString() + testSummaryTitle }
                    this.summaryDescription = { testSummaryText }
                    this.actions {
                        add(Action(
                                R.drawable.ic_app_icon,
                                testActionText,
                                testActionIntent
                        ))
                    }
                }
                .asBuilder()
                .build()

        Assert.assertEquals(testTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
        Assert.assertEquals(testText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT))
        Assert.assertEquals(testKey, NotifyExtender.getKey(notification.extras))
        Assert.assertNull(notification.contentIntent)
        Assert.assertEquals(testSummaryContent, NotifyExtender.getExtensions(notification.extras).getCharSequence(NotifyExtender.SUMMARY_CONTENT))
        Assert.assertNull(notification.actions)
    }
}

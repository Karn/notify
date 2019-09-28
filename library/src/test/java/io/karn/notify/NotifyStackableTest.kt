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

package io.karn.notify

import android.app.PendingIntent
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationCompat
import io.karn.notify.internal.NotificationInterop
import io.karn.notify.internal.NotifyExtender
import io.karn.notify.internal.utils.Action
import io.karn.notify.internal.utils.Errors
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class NotifyStackableTest : NotifyTestBase() {

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

        Assert.assertNull(notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE))
        Assert.assertEquals(testTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
        Assert.assertEquals(testText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT))
        Assert.assertEquals(testKey, NotifyExtender.getKey(notification.extras))
        Assert.assertNull(notification.contentIntent)
        Assert.assertEquals(testSummaryContent, NotifyExtender.getExtensions(notification.extras).getCharSequence(NotifyExtender.SUMMARY_CONTENT))
        Assert.assertNull(notification.actions)
    }

    @Test
    fun multipleStackableTest() {
        val testTitle = "New dessert menu"
        val testText = "The Cheesecake Factory has a new dessert for you to try!"
        val testKey = "test_key"
        val testClickIntent = PendingIntent.getActivity(this.context, 0, Intent(Settings.ACTION_SETTINGS), 0)
        val testSummaryContent = "New desserts available to try!"
        val testSummaryTitle = " new dessert menus"
        val testSummaryText = "Try out these delicious new menu items today!"
        val testActionText = "Action"
        val testActionIntent = PendingIntent.getActivity(this.context, 0, Intent(Settings.ACTION_SYNC_SETTINGS), 0)

        val notificationRaw = Notify.with(this.context)
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

        // Show once.
        notificationRaw.show()
        notificationRaw.show()

        // They should have collapsed.
        Assert.assertEquals(1, NotificationInterop.getActiveNotifications(Notify.defaultConfig.notificationManager!!).size)

        // Build to test expected notification contents.
        val notification = notificationRaw
                .asBuilder()
                .build()

        // Assert.assertEquals("android.app.Notification\$InboxStyle", notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE).toString())
        Assert.assertEquals("3$testSummaryTitle", notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE))
        Assert.assertEquals(testSummaryText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString())
        Assert.assertEquals(testKey, NotifyExtender.getKey(notification.extras))
        Assert.assertEquals(testClickIntent, notification.contentIntent)
        Assert.assertEquals(testSummaryContent, NotifyExtender.getExtensions(notification.extras).getCharSequence(NotifyExtender.SUMMARY_CONTENT))
        Assert.assertEquals(
                listOf(testSummaryContent, testSummaryContent, testSummaryContent),
                notification.extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)?.toList())
        Assert.assertEquals(1, notification.actions.size)
        Assert.assertEquals(testActionText, notification.actions.first().title)
        Assert.assertEquals(testActionIntent, notification.actions.first().actionIntent)
    }
}

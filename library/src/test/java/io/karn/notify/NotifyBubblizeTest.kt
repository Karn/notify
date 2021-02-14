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
import androidx.core.graphics.drawable.IconCompat
import io.karn.notify.internal.NotificationInterop
import io.karn.notify.internal.NotifyExtender
import io.karn.notify.internal.utils.Action
import io.karn.notify.internal.utils.Errors
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotifyBubblizeTest : NotifyTestBase() {

    @Test
    fun defaultBubblizeTest() {
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
    }

    @Test
    fun invalidRequiredArgsTest() {
        val testTitle = "New dessert menu"
        val testText = "The Cheesecake Factory has a new dessert for you to try!"

        var exceptionThrown: IllegalArgumentException? = null

        try {
            Notify.with(this.context)
                    .content {
                        title = testTitle
                        text = testText
                    }
                    .bubblize {
                        // bubbleIcon
                        targetActivity = PendingIntent.getActivity(this@NotifyBubblizeTest.context, 0, Intent(Settings.ACTION_SETTINGS), 0)
                    }
                    .asBuilder()
                    .build()
        } catch (e: IllegalArgumentException) {
            exceptionThrown = e
        }

        Assert.assertNotNull(exceptionThrown)
        Assert.assertEquals(Errors.INVALID_BUBBLE_ICON_ERROR, exceptionThrown?.message)

        try {
            Notify.with(this.context)
                    .content {
                        title = testTitle
                        text = testText
                    }
                    .bubblize {
                        bubbleIcon = IconCompat.createWithResource(this@NotifyBubblizeTest.context, R.drawable.ic_app_icon)
                        // targetActivity
                    }
                    .asBuilder()
                    .build()
        } catch (e: IllegalArgumentException) {
            exceptionThrown = e
        }

        Assert.assertNotNull(exceptionThrown)
        Assert.assertEquals(Errors.INVALID_BUBBLE_TARGET_ACTIVITY_ERROR, exceptionThrown?.message)
    }
}

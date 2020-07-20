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

import android.app.Notification
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
        val testGroup = "test_group"
        val testTimeout = 5000L

        val notification = Notify.with(this.context)
                .meta {
                    clickIntent = testClickIntent
                    clearIntent = testClearIntent
                    cancelOnClick = testCancelOnClick
                    category = testCategory
                    group = testGroup
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
        Assert.assertEquals(testGroup, notification.group)
        Assert.assertEquals(testTimeout, notification.timeoutAfter)
        Assert.assertEquals(1, notification.extras.getStringArrayList(Notification.EXTRA_PEOPLE_LIST)?.size
                ?: 0)
    }
}

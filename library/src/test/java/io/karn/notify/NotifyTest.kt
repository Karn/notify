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

import android.graphics.Color
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import io.karn.notify.internal.NotificationInterop
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NotifyTest : NotifyTestBase() {

    @Test
    fun initializationTest() {
        val testIcon = R.drawable.ic_android_black
        val testColor = android.R.color.darker_gray

        val testVisibility = NotificationCompat.VISIBILITY_PUBLIC
        val testChannelKey = "test_key_alt"
        val testChannelName = "Test Channel"
        val testChannelDescription = "Test Channel Description"
        val testChannelImportance = Notify.IMPORTANCE_HIGH
        val testLightColor = Color.CYAN
        val testVibrationPattern = listOf<Long>(0, 200, 0, 200)
        val testSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        Notify.defaultConfig {
            header {
                icon = testIcon
                color = testColor
            }

            alerting(testChannelKey) {
                lockScreenVisibility = testVisibility
                channelName = testChannelName
                channelDescription = testChannelDescription
                channelImportance = testChannelImportance
                lightColor = testLightColor
                vibrationPattern = testVibrationPattern
                sound = testSound
            }
        }

        val rawNotification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }

        Assert.assertEquals(testIcon, rawNotification.asBuilder().build().smallIcon.resId)

        rawNotification.show()

        val shadowChannel = shadowNotificationManager.getNotificationChannel(testChannelKey)
        Assert.assertNotNull(shadowChannel)
        Assert.assertEquals(testVisibility, shadowChannel.lockscreenVisibility)
        Assert.assertEquals(testChannelName, shadowChannel.name)
        Assert.assertEquals(testChannelDescription, shadowChannel.description)
        Assert.assertEquals(testChannelImportance + 3, shadowChannel.importance)
        // Assert.assertEquals(testLightColor, shadowChannel.lightColor)
        Assert.assertEquals(testVibrationPattern, shadowChannel.vibrationPattern.toList())
        Assert.assertEquals(testSound, shadowChannel.sound)
    }

    @Test
    fun showNotification() {
        Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        Assert.assertEquals(1, NotificationInterop.getActiveNotifications(shadowNotificationManager).size)
    }

    @Test
    fun showNotificationWithId() {
        val expectedId = 10
        val actualId = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show(expectedId)

        Assert.assertEquals(expectedId, actualId)
        Assert.assertEquals(1, NotificationInterop.getActiveNotifications(shadowNotificationManager).size)
    }

    @Test
    fun cancelNotification() {
        val notificationId = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        Assert.assertEquals(1, NotificationInterop.getActiveNotifications(shadowNotificationManager).size)

        Notify.cancelNotification(context, notificationId)

        Assert.assertEquals(0, NotificationInterop.getActiveNotifications(shadowNotificationManager).size)
    }
}

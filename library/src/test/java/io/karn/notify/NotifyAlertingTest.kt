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
import android.os.Build
import androidx.core.app.NotificationCompat
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
class NotifyAlertingTest : NotifyTestBase() {

    @After
    fun runAfter() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, currentSdkVersion)
    }

    @Test
    fun defaultAlertingTest_onAndroidN() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.N_MR1)

        val testAlerting = Notify.defaultConfig.defaultAlerting

        val notification = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertNull(notification.channelId)
        Assert.assertEquals(testAlerting.lockScreenVisibility, notification.visibility)
        Assert.assertEquals(testAlerting.channelImportance, notification.priority)
        // Color comparison nonsense again.
        // Assert.assertEquals(testLightColor, notification.color)
        Assert.assertNull(notification.vibrate)
        Assert.assertEquals(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), notification.sound)
    }

    @Test
    fun defaultAlertingTest_onAndroidO() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.O)

        val testAlerting = Notify.defaultConfig.defaultAlerting

        Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        val shadowChannel = shadowNotificationManager.getNotificationChannel(testAlerting.channelKey)
        Assert.assertNotNull(shadowChannel)
        Assert.assertEquals(testAlerting.lockScreenVisibility, shadowChannel.lockscreenVisibility)
        Assert.assertEquals(testAlerting.channelName, shadowChannel.name)
        Assert.assertEquals(testAlerting.channelDescription, shadowChannel.description)
        Assert.assertEquals(testAlerting.channelImportance + 3, shadowChannel.importance)
        // Assert.assertEquals(testLightColor, shadowChannel.lightColor)
        Assert.assertNull(shadowChannel.vibrationPattern)
        Assert.assertEquals(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), shadowChannel.sound)
    }

    @Test
    fun modifiedAlertingTest_onAndroidN() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.N_MR1)

        val testVisibility = NotificationCompat.VISIBILITY_PUBLIC
        val testChannelKey = "test_key"
        val testChannelName = "Test Channel"
        val testChannelDescription = "Test Channel Description"
        val testChannelImportance = Notify.IMPORTANCE_HIGH
        val testLightColor = Color.CYAN
        val testVibrationPattern = listOf<Long>(0, 200, 0, 200)
        val testSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        val notification = Notify.with(this.context)
                .alerting(testChannelKey) {
                    lockScreenVisibility = testVisibility
                    channelName = testChannelName
                    channelDescription = testChannelDescription
                    channelImportance = testChannelImportance
                    lightColor = testLightColor
                    vibrationPattern = testVibrationPattern
                    sound = testSound
                }
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .asBuilder()
                .build()

        Assert.assertNull(notification.channelId)
        Assert.assertEquals(testVisibility, notification.visibility)
        Assert.assertEquals(testChannelImportance, notification.priority)
        // Color comparison nonsense again.
        // Assert.assertEquals(testLightColor, notification.color)
        Assert.assertEquals(testVibrationPattern, notification.vibrate.asList())
        Assert.assertEquals(testSound, notification.sound)
    }

    @Test
    fun modifiedAlertingTest_onAndroidO() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.O)

        val testVisibility = NotificationCompat.VISIBILITY_PUBLIC
        val testChannelKey = "test_key"
        val testChannelName = "Test Channel"
        val testChannelDescription = "Test Channel Description"
        val testChannelImportance = Notify.IMPORTANCE_HIGH
        val testLightColor = Color.CYAN
        val testVibrationPattern = listOf<Long>(0, 200, 0, 200)
        val testSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)

        Notify.with(this.context)
                .alerting(testChannelKey) {
                    lockScreenVisibility = testVisibility
                    channelName = testChannelName
                    channelDescription = testChannelDescription
                    channelImportance = testChannelImportance
                    lightColor = testLightColor
                    vibrationPattern = testVibrationPattern
                    sound = testSound
                }
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

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
}

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

import android.os.Build
import io.karn.notify.entities.Payload
import io.karn.notify.internal.NotificationChannelInterop
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers


@RunWith(RobolectricTestRunner::class)
class NotificationChannelInteropTest : NotifyTestBase() {

    @After
    fun runAfter() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, currentSdkVersion)
    }

    @Test
    fun registerChannelTest_onAndroidN() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.N_MR1)

        val registeredChannel = NotificationChannelInterop.with(Payload.Alerts())

        Assert.assertFalse(registeredChannel)
    }

    @Test
    fun registerChannelTest_onAndroidO() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.O)

        val testAlerting = Payload.Alerts()
        val registeredChannel = NotificationChannelInterop.with(testAlerting)

        Assert.assertTrue(registeredChannel)
        Assert.assertNotNull(shadowNotificationManager.getNotificationChannel(testAlerting.channelKey))
    }
}

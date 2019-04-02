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

import android.app.Application
import android.app.NotificationManager
import android.os.Build
import io.karn.notify.entities.Payload
import io.karn.notify.internal.NotificationChannelInterop
import org.junit.Before
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadow.api.Shadow

open class NotifyTestBase {

    companion object {
        @JvmStatic
        protected val SDK_INT = "SDK_INT"
        @JvmStatic
        protected var currentSdkVersion = Build.VERSION.SDK_INT
    }

    protected val context: Application = RuntimeEnvironment.application
    protected var shadowNotificationManager: NotificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

    @Before
    fun setNotificationManager() {
        shadowNotificationManager = Shadow.newInstanceOf(NotificationManager::class.java)
        Notify.defaultConfig {
            defaultHeader = Payload.Header()
            defaultAlerting = Payload.Alerts()
            notificationManager = shadowNotificationManager
        }
        NotificationChannelInterop.with(Notify.defaultConfig.defaultAlerting)
    }
}

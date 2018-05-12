package io.karn.notify

import android.app.Application
import android.app.NotificationManager
import android.os.Build
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
    protected val shadowNotificationManager: NotificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

    @Before
    fun resetNotificationManager() {
        Notify.defaultConfig {
            notificationManager = shadowNotificationManager
        }
    }
}

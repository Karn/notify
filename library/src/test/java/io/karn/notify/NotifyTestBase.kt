package io.karn.notify

import android.app.Application
import android.app.NotificationManager
import org.junit.Before
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadow.api.Shadow

open class NotifyTestBase {
    protected val context: Application = RuntimeEnvironment.application

    @Before
    fun resetNotificationManager() {
        val notificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

        Notify.defaultConfig {
            it.notificationManager = notificationManager
        }
    }
}

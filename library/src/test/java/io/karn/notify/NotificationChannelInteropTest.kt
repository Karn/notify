package io.karn.notify

import android.app.NotificationManager
import android.os.Build
import io.karn.notify.entities.Payload
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadow.api.Shadow
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

        val shadowNotificationManager = Shadow.newInstanceOf(NotificationManager::class.java)
        Notify.defaultConfig {
            notificationManager = shadowNotificationManager
        }

        val testAlerting = Payload.Alerts()


        val registeredChannel = NotificationChannelInterop.with(testAlerting)

        Assert.assertTrue(registeredChannel)
        Assert.assertNotNull(shadowNotificationManager.getNotificationChannel(testAlerting.channelKey))
    }
}

package io.karn.notify

import android.app.NotificationManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadow.api.Shadow
import org.robolectric.util.ReflectionHelpers


@RunWith(RobolectricTestRunner::class)
class NotifyChannelTest : NotifyTestBase() {

    private var currentSdkVersion = Build.VERSION.SDK_INT

    @After
    fun runAfter() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", currentSdkVersion)
    }

    @Test
    fun registerChannelTest_onAndroidO() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.O)

        val notificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

        val registeredChannel = NotifyChannel.registerChannel(notificationManager,
                Notify.DEFAULT_CHANNEL_KEY,
                Notify.DEFAULT_CHANNEL_NAME,
                Notify.DEFAULT_CHANNEL_DESCRIPTION,
                NotificationCompat.PRIORITY_DEFAULT)

        Assert.assertTrue(registeredChannel)
        Assert.assertNotNull(notificationManager.getNotificationChannel(Notify.DEFAULT_CHANNEL_KEY))
    }

    @Test
    fun registerChannelTest_onAndroidN() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.N_MR1)

        val notificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

        val registeredChannel = NotifyChannel.registerChannel(notificationManager,
                Notify.DEFAULT_CHANNEL_KEY,
                Notify.DEFAULT_CHANNEL_NAME,
                Notify.DEFAULT_CHANNEL_DESCRIPTION,
                NotificationCompat.PRIORITY_DEFAULT)

        Assert.assertFalse(registeredChannel)
    }
}

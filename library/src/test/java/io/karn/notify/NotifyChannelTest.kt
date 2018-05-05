package io.karn.notify

import android.os.Build
import android.support.v4.app.NotificationCompat
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.util.ReflectionHelpers


@RunWith(RobolectricTestRunner::class)
class NotifyChannelTest {

    private val context = RuntimeEnvironment.application
    private var currentSdkVersion = Build.VERSION.SDK_INT

    @After
    fun runAfter() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", currentSdkVersion)
    }

    @Test
    fun registerChannelTest_onAndroidO() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.O)

        val registeredChannel = NotifyChannel.registerChannel(context,
                Notify.DEFAULT_CHANNEL_KEY,
                Notify.DEFAULT_CHANNEL_NAME,
                Notify.DEFAULT_CHANNEL_DESCRIPTION,
                NotificationCompat.PRIORITY_DEFAULT)

        Assert.assertTrue(registeredChannel)
    }

    @Test
    fun registerChannelTest_onAndroidN() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", Build.VERSION_CODES.N_MR1)

        val registeredChannel = NotifyChannel.registerChannel(context,
                Notify.DEFAULT_CHANNEL_KEY,
                Notify.DEFAULT_CHANNEL_NAME,
                Notify.DEFAULT_CHANNEL_DESCRIPTION,
                NotificationCompat.PRIORITY_DEFAULT)

        Assert.assertFalse(registeredChannel)
    }
}

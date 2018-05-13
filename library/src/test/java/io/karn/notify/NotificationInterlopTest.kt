package io.karn.notify

import android.os.Build
import android.support.v4.app.NotificationCompat
import io.karn.notify.internal.NotificationInterop
import junit.framework.Assert
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
class NotificationInterlopTest : NotifyTestBase() {

    @After
    fun runAfter() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, currentSdkVersion)
    }

    @Test
    fun getActiveNotifications_onAndroidLollipop() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.LOLLIPOP_MR1)

        Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        val notifications = NotificationInterop.getActiveNotifications(shadowNotificationManager)
        Assert.assertEquals(0, notifications.size)
    }

    @Test
    fun getActiveNotifications_onAndroidM() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, SDK_INT, Build.VERSION_CODES.M)

        Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        val ogNotification = NotificationCompat.Builder(this.context, Notify.defaultConfig.defaultAlerting.channelKey)
                .setContentTitle("Title")
                .setContentText("Text")
                .build()

        shadowNotificationManager.notify(123, ogNotification)

        val allNotfications = shadowNotificationManager.activeNotifications
        Assert.assertEquals(2, allNotfications.size)
        val notifyNotifications = NotificationInterop.getActiveNotifications(shadowNotificationManager)
        Assert.assertEquals(1, notifyNotifications.size)
    }
}

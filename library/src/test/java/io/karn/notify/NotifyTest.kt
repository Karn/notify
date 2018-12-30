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
        Assert.assertEquals(testChannelImportance + 2, shadowChannel.importance)
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
    fun cancelNotification() {
        val notificationId = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        Assert.assertEquals(1, NotificationInterop.getActiveNotifications(shadowNotificationManager).size)

        Notify.with(this.context)
                .cancel(notificationId)

        Assert.assertEquals(0, NotificationInterop.getActiveNotifications(shadowNotificationManager).size)
    }
}

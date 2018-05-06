package io.karn.notify

import android.app.NotificationManager
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadow.api.Shadow

@RunWith(RobolectricTestRunner::class)
class NotifyTest {

    private val context = RuntimeEnvironment.application

    @Test
    fun initializationTest() {
        Notify.defaultConfig {
            it.header.icon = R.drawable.ic_android_black
            it.header.color = android.R.color.darker_gray
        }
    }

    @Test
    fun showNotification() {
        val notificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

        Notify.defaultConfig {
            it.notificationManager = notificationManager
        }

        Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        Assert.assertEquals(1, NotificationInterop.getActiveNotifications(notificationManager).size)
    }

    @Test
    fun cancelNotification() {
        // TODO: Inject existing notifications so there is no code duplication.
        val notificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

        Notify.defaultConfig {
            it.notificationManager = notificationManager
        }

        val notificationId = Notify.with(this.context)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .show()

        Assert.assertEquals(1, NotificationInterop.getActiveNotifications(notificationManager).size)

        Notify.with(this.context)
                .cancel(notificationId)

        Assert.assertEquals(0, NotificationInterop.getActiveNotifications(notificationManager).size)
    }
}

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

            header {
                icon = R.drawable.ic_android_black
                color = android.R.color.darker_gray
            }
        }
    }

    @Test
    fun showNotification() {
        val shadowNotificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

        Notify.defaultConfig {
            notificationManager = shadowNotificationManager
        }

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
        // TODO: Inject existing notifications so there is no code duplication.
        val shadowNotificationManager = Shadow.newInstanceOf(NotificationManager::class.java)

        Notify.defaultConfig {
            notificationManager = shadowNotificationManager
        }

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

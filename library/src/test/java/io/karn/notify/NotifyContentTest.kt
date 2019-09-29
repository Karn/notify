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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.core.app.NotificationCompat
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NotifyContentTest {

    companion object {

        private const val KEY_TEXT = "text"
        private const val KEY_TIMESTAMP = "time"
        private const val KEY_SENDER = "sender"
        private const val KEY_DATA_MIME_TYPE = "type"
        private const val KEY_DATA_URI = "uri"
        private const val KEY_EXTRAS_BUNDLE = "extras"

        private fun getMessagesFromBundleArray(bundles: Array<Parcelable>): List<NotificationCompat.MessagingStyle.Message> {
            val messages = ArrayList<NotificationCompat.MessagingStyle.Message>(bundles.size)
            bundles.indices
                    .filter { bundles[it] is Bundle }
                    .mapNotNullTo(messages) { getMessageFromBundle(bundles[it] as Bundle) }
            return messages
        }

        private fun getMessageFromBundle(bundle: Bundle): NotificationCompat.MessagingStyle.Message? {
            try {
                return if (!bundle.containsKey(KEY_TEXT) || !bundle.containsKey(KEY_TIMESTAMP)) {
                    null
                } else {
                    val message = NotificationCompat.MessagingStyle.Message(bundle.getCharSequence(KEY_TEXT),
                            bundle.getLong(KEY_TIMESTAMP), bundle.getCharSequence(KEY_SENDER))
                    if (bundle.containsKey(KEY_DATA_MIME_TYPE) && bundle.containsKey(KEY_DATA_URI)) {
                        message.setData(bundle.getString(KEY_DATA_MIME_TYPE),
                                bundle.getParcelable<Parcelable>(KEY_DATA_URI) as Uri)
                    }
                    if (bundle.containsKey(KEY_EXTRAS_BUNDLE)) {
                        message.extras.putAll(bundle.getBundle(KEY_EXTRAS_BUNDLE))
                    }
                    message
                }
            } catch (e: ClassCastException) {
                return null
            }

        }
    }

    private val context: Application = RuntimeEnvironment.application

    @Test
    fun defaultNotification() {
        val testTitle = "New dessert menu"
        val testText = "The Cheesecake Factory has a new dessert for you to try!"
        val testLargeIconResID = R.drawable.notification_tile_bg

        val notification = Notify.with(this.context)
                .content {
                    title = testTitle
                    text = testText
                    largeIcon = BitmapFactory.decodeResource(context.resources, testLargeIconResID)
                }
                .asBuilder()
                .build()

        Assert.assertNull(notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE))
        Assert.assertEquals(testTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE).toString())
        Assert.assertEquals(testText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString())
        // Assert.assertEquals(context.resources.getDrawable(testLargeIconResID, context.theme), notification.getLargeIcon().loadDrawable(this.context))
    }

    @Test
    fun textListNotification() {
        val testLines = ArrayList<CharSequence>()
                .apply {
                    add("New! Fresh Strawberry Cheesecake.")
                    add("New! Salted Caramel Cheesecake.")
                    add("New! OREO Dream Dessert.")
                }

        val testTitle = "New menu items!"
        val testText = testLines.size.toString() + " new dessert menu items found."

        val notification = Notify.with(this.context)
                .asTextList {
                    lines = testLines
                    title = testTitle
                    text = testText
                }
                .asBuilder()
                .build()

        Assert.assertEquals("android.app.Notification\$InboxStyle", notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE).toString())
        Assert.assertEquals(testTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE).toString())
        Assert.assertEquals(testText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString())

        Assert.assertEquals(testLines, notification.extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES)?.toList())
    }

    @Test
    fun bigTextNotification() {
        val testTitle = "Chocolate brownie sundae"
        val testText = "Try our newest dessert option!"
        val testExpandedText = "Try our newest dessert option!"
        val testBigText = "Our own Fabulous Godiva Chocolate Brownie, Vanilla Ice Cream, Hot Fudge, Whipped Cream and Toasted Almonds.\n" +
                "\n" +
                "Come try this delicious new dessert and get two for the price of one!"

        val notification = Notify.with(this.context)
                .asBigText {
                    title = testTitle
                    text = testText
                    expandedText = testExpandedText
                    bigText = testBigText
                }
                .asBuilder()
                .build()

        Assert.assertEquals("android.app.Notification\$BigTextStyle", notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE).toString())
        Assert.assertEquals(testTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE).toString())
        Assert.assertEquals(testText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString())
        Assert.assertEquals(testExpandedText + "\n" + testBigText, notification.extras.getCharSequence(NotificationCompat.EXTRA_BIG_TEXT).toString())
    }

    @Test
    fun bigPictureNotification() {
        val testTitle = "Chocolate brownie sundae"
        val testText = "Get a look at this amazing dessert!"
        val testCollapsedText = "The delicious brownie sundae now available."
        val testLargeIconResID = R.drawable.notification_tile_bg
        val testImage = BitmapFactory.decodeResource(context.resources, R.drawable.notification_tile_bg)
        Assert.assertNotNull(testImage)

        val notification = Notify.with(this.context)
                .asBigPicture {
                    title = testTitle
                    text = testText
                    image = testImage
                    expandedText = testCollapsedText
                    largeIcon = BitmapFactory.decodeResource(context.resources, testLargeIconResID)
                }
                .asBuilder()
                .build()

        Assert.assertEquals("android.app.Notification\$BigPictureStyle", notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE).toString())
        Assert.assertEquals(testTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_TITLE).toString())
        Assert.assertEquals(testText, notification.extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString())
        // This is an example of Notifications vague methods. The Builder#setSummaryText is
        // different from the Style#setSummaryText.
        Assert.assertEquals(testCollapsedText, notification.extras.getCharSequence(NotificationCompat.EXTRA_SUMMARY_TEXT))
        // Assert.assertEquals(context.resources.getDrawable(testLargeIconResID, context.theme), notification.getLargeIcon().loadDrawable(this.context))

        val actualIcon: Icon? = notification.extras.getParcelable(NotificationCompat.EXTRA_LARGE_ICON)
        Assert.assertNotNull(actualIcon)

        val actualImage: Bitmap? = notification.extras.getParcelable(NotificationCompat.EXTRA_PICTURE)
        Assert.assertNotNull(actualImage)

        Assert.assertEquals(testImage, actualImage)
    }

    @Test
    fun messageNotification() {
        val testUserDisplayName = "Karn"
        val testConversationTitle = "Sundae chat"
        val testMessages = ArrayList<NotificationCompat.MessagingStyle.Message>()
                .apply {

                    add(NotificationCompat.MessagingStyle.Message("Are you guys ready to try the Strawberry sundae?",
                            System.currentTimeMillis() - (6 * 60 * 1000), // 6 Mins ago
                            "Karn"))

                    add(NotificationCompat.MessagingStyle.Message("Yeah! I've heard great things about this place.",
                            System.currentTimeMillis() - (5 * 60 * 1000), // 5 Mins ago
                            "Nitish"))

                    add(NotificationCompat.MessagingStyle.Message("What time are you getting there Karn?",
                            System.currentTimeMillis() - (1 * 60 * 1000), // 1 Mins ago
                            "Moez"))
                }

        val notification = Notify.with(this.context)
                .asMessage {
                    userDisplayName = testUserDisplayName
                    conversationTitle = testConversationTitle
                    messages = testMessages
                }
                .asBuilder()
                .build()

        Assert.assertEquals("android.app.Notification\$MessagingStyle", notification.extras.getCharSequence(NotificationCompat.EXTRA_TEMPLATE).toString())
        Assert.assertEquals(testUserDisplayName, notification.extras.getCharSequence(NotificationCompat.EXTRA_SELF_DISPLAY_NAME))
        Assert.assertEquals(testConversationTitle, notification.extras.getCharSequence(NotificationCompat.EXTRA_CONVERSATION_TITLE))

        val actualMessages = notification.extras.getParcelableArray(NotificationCompat.EXTRA_MESSAGES)?.let { getMessagesFromBundleArray(it) }
        Assert.assertNotNull(actualMessages)
        Assert.assertEquals(testMessages.size, actualMessages!!.size)

        actualMessages.forEach { message ->
            testMessages[actualMessages.indexOf(message)].let {
                Assert.assertEquals(message.text, it.text)
                Assert.assertEquals(message.timestamp, it.timestamp)
                Assert.assertEquals(message.sender, it.sender)
            }
        }
    }
}

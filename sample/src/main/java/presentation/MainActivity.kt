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

package presentation

import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import io.karn.notify.Notify
import io.karn.notify.sample.R
import java.util.Arrays

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Notify.defaultConfig {
            header {
                color = resources.getColor(R.color.colorPrimaryDark)
            }
            alerting(Notify.CHANNEL_DEFAULT_KEY) {
                lightColor = Color.RED
            }
        }
    }

    fun notifyDefault(view: View) {
        Notify.with(this)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .stackable {
                    key = "test_key"
                    summaryContent = "test summary content"
                    summaryTitle = { count -> "Summary title" }
                    summaryDescription = { count -> count.toString() + " new notifications." }
                }
                .show()
    }

    fun notifyTextList(view: View) {
        Notify.with(this)
                .asTextList {
                    lines = Arrays.asList("New! Fresh Strawberry Cheesecake.",
                            "New! Salted Caramel Cheesecake.",
                            "New! OREO Dream Dessert.")
                    title = "New menu items!"
                    text = lines.size.toString() + " new dessert menu items found."
                }
                .show()

    }

    fun notifyBigText(view: View) {
        Notify.with(this)
                .asBigText {
                    title = "Chocolate brownie sundae"
                    text = "Try our newest dessert option!"
                    expandedText = "Mouthwatering deliciousness."
                    bigText = "Our own Fabulous Godiva Chocolate Brownie, Vanilla Ice Cream, Hot Fudge, Whipped Cream and Toasted Almonds.\n" +
                            "\n" +
                            "Come try this delicious new dessert and get two for the price of one!"
                }
                .show()
    }

    fun notifyBigPicture(view: View) {
        Notify.with(this)
                .asBigPicture {
                    title = "Chocolate brownie sundae"
                    text = "Get a look at this amazing dessert!"
                    expandedText = "The delicious brownie sundae now available."
                    image = BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.chocolate_brownie_sundae)
                }
                .show()
    }

    fun notifyMessage(view: View) {
        Notify.with(this)
                .asMessage {
                    userDisplayName = "Karn"
                    conversationTitle = "Sundae chat"
                    messages = Arrays.asList(
                            NotificationCompat.MessagingStyle.Message("Are you guys ready to try the Strawberry sundae?",
                                    System.currentTimeMillis() - (6 * 60 * 1000), // 6 Mins ago
                                    "Karn"),
                            NotificationCompat.MessagingStyle.Message("Yeah! I've heard great things about this place.",
                                    System.currentTimeMillis() - (5 * 60 * 1000), // 5 Mins ago
                                    "Nitish"),
                            NotificationCompat.MessagingStyle.Message("What time are you getting there Karn?",
                                    System.currentTimeMillis() - (1 * 60 * 1000), // 1 Mins ago
                                    "Moez")
                    )
                }
                .show()
    }

    fun notifyBubble(view: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(this, "Notification Bubbles are only supported on a device running Android Q or later.", Toast.LENGTH_SHORT).show()
            return
        }

        Notify.with(this)
                .content {
                    title = "New dessert menu"
                    text = "The Cheesecake Factory has a new dessert for you to try!"
                }
                .bubblize {
                    // Create bubble intent
                    val target = Intent(this@MainActivity, BubbleActivity::class.java)
                    val bubbleIntent = PendingIntent.getActivity(this@MainActivity, 0, target, 0 /* flags */)

                    bubbleIcon = IconCompat.createWithResource(this@MainActivity, R.drawable.ic_app_icon)
                    targetActivity = bubbleIntent
                    suppressInitialNotification = true
                }
                .show()
    }

    fun notifyIndeterminateProgress(view: View) {

        Notify.with(this)
                .asBigText {
                    title = "Uploading files"
                    expandedText = "The files are being uploaded!"
                    bigText = "Daft Punk - Get Lucky.flac is uploading to server /music/favorites"
                }
                .progress {
                    showProgress = true
                }
                .show()
    }

    fun notifyDeterminateProgress(view: View) {

        Notify.with(this)
                .asBigText {
                    title = "Bitcoin payment processing"
                    expandedText = "Your payment was sent to the Bitcoin network"
                    bigText = "Your payment #0489 is being confirmed 2/4"
                }
                .progress {
                    showProgress = true
                    enablePercentage = true
                    progressPercent = 30
                }
                .show()
    }
}

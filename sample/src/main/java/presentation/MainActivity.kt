package presentation

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.karn.notify.Notify
import io.karn.notify.sample.R
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Notify.defaultConfig {
            header {
                color = R.color.colorPrimaryDark
            }
            alerting(Notify.CHANNEL_DEFAULT_KEY) {
                lightColor = Color.RED
            }
        }
    }

    fun notifyDefault(view: View) {
        Notify
                .with(this)
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
        Notify
                .with(this)
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
        Notify
                .with(this)
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
        Notify
                .with(this)
                .asBigPicture {
                    title = "Chocolate brownie sundae"
                    text = "Get a look at this amazing dessert!"
                    expandedText = "The delicious brownie sundae now available."
                    image = BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.chocolate_brownie_sundae)
                }
                .show()
    }

    fun notifyMessage(view: View) {
        Notify
                .with(this)
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
}

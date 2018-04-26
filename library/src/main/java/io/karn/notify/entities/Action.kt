package io.karn.notify.entities

import android.app.PendingIntent
import android.support.v4.app.NotificationCompat

class Action(icon: Int, title: CharSequence?, intent: PendingIntent?) : NotificationCompat.Action(icon, title, intent)

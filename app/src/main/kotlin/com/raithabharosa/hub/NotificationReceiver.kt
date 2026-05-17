package com.raithabharosa.hub

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val notification = intent.getParcelableExtra("notification") as? android.app.Notification
            val taskId = intent.getIntExtra("taskId", 0)

            if (notification != null) {
                NotificationManagerCompat.from(context).notify(taskId, notification)
            }
        }
    }
}

package com.raithabharosa.hub

import android.app.Service
import android.content.Intent
import android.media.RingtoneManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val taskId = intent.getIntExtra("taskId", 0)
            val title = intent.getStringExtra("title") ?: "Task Reminder"
            val notes = intent.getStringExtra("notes") ?: "Time for your task!"

            Log.d("NotificationService", "Showing notification for task: $title")

            try {
                val notification = NotificationCompat.Builder(this, "task_channel")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle("Task Reminder: $title")
                    .setContentText(notes)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(longArrayOf(0, 500, 250, 500))
                    .setAutoCancel(true)
                    .build()

                NotificationManagerCompat.from(this).notify(taskId, notification)
                Log.d("NotificationService", "Notification shown successfully for task $taskId")
            } catch (e: Exception) {
                Log.e("NotificationService", "Error showing notification: ${e.message}", e)
            }
        }

        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

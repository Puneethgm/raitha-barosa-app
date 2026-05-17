package com.raithabharosa.hub

import android.content.Context
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        return try {
            val taskId = inputData.getInt("taskId", 0)
            val title = inputData.getString("title") ?: "Task Reminder"
            val notes = inputData.getString("notes") ?: "Time for your task!"

            Log.d("NotificationWorker", "Showing notification for task: $title")

            val notification = NotificationCompat.Builder(applicationContext, "task_channel")
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Task Reminder: $title")
                .setContentText(notes)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(longArrayOf(0, 500, 250, 500))
                .setAutoCancel(true)
                .build()

            NotificationManagerCompat.from(applicationContext).notify(taskId, notification)
            Log.d("NotificationWorker", "Notification shown successfully for task $taskId")
            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error showing notification: ${e.message}", e)
            Result.retry()
        }
    }
}

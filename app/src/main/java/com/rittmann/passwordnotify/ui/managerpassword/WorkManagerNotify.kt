package com.rittmann.passwordnotify.ui.managerpassword

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rittmann.passwordnotify.R
import java.util.concurrent.TimeUnit

const val ID = "id"
const val TITLE = "title"
const val DESCRIPTION = "description"

class WorkManagerNotify {

    fun sendPeriodic(context: Context, timeToSend: Long, notification: Notification) {
        val builder = Data.Builder()
        builder.putLong(ID, notification.id)
        builder.putString(TITLE, notification.title)
        builder.putString(DESCRIPTION, notification.message)

        val timeUnit = TimeUnit.MINUTES
        PeriodicWorkRequest.Builder(
            WorkerNotify::class.java,
            timeToSend,
            timeUnit
        ).apply {
            setInitialDelay(timeToSend, timeUnit)
            setInputData(builder.build())

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                notification.id.toString(),
                ExistingPeriodicWorkPolicy.REPLACE,
                build()
            )
        }
    }
}

class WorkerNotify(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {

        val id = inputData.getLong(ID, 1L)
        val title = inputData.getString(TITLE)
        val description = inputData.getString(DESCRIPTION)

        NotificationController().create(context, id, title, description)
        return Result.success()
    }
}

class Notification(val id: Long, val title: String?, val message: String?)

class NotificationController {
    private val vibrate = longArrayOf(2000, 1000, 2000, 1000)
    private val chanelId = "password_generate_x011"

    fun create(context: Context, notificationId: Long, title: String?, message: String?) {
        val builder = NotificationCompat.Builder(context, chanelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setAutoCancel(true)
            .setVibrate(vibrate)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notify(context, notificationId, builder)
    }

    private fun notify(context: Context, notificationId: Long, builder: NotificationCompat.Builder) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context)
        notificationManager.notify(notificationId.toInt(), builder.build())
    }

    private fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = context.getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(chanelId, name, importance)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }
}
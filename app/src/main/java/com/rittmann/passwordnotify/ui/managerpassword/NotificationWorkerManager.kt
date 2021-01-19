package com.rittmann.passwordnotify.ui.managerpassword

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rittmann.passwordnotify.data.basic.ManagerPassword
import java.util.concurrent.TimeUnit

object NotificationWorkerManager {

    fun scheduleNextNotification(context: Context, time: Long, tagId: String, managerPassword: ManagerPassword) {
        val workBuilder =
            PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                time,
                TimeUnit.MINUTES
            )
        val worker = workBuilder.build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            tagId,
            ExistingPeriodicWorkPolicy.KEEP,
            worker
        )
    }

    fun cancel(context: Context, tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }
}

class NotificationWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    override fun doWork(): Result {

        return Result.success()
    }
}
package uz.juo.triple.utils

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import java.net.SocketException
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class ReminderWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val REMINDER_WORK_NAME = "reminder"
        private const val PARAM_NAME = "name"

        @RequiresApi(Build.VERSION_CODES.O)
        fun runAt() {
            val workManager = WorkManager.getInstance()

            // trigger at 8:30am
            val alarmTime = LocalTime.of(8, 30)
            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            val nowTime = now.toLocalTime()
            // if same time, schedule for next day as well
            // if today's time had passed, schedule for next day
            if (nowTime == alarmTime || nowTime.isAfter(alarmTime)) {
                now = now.plusDays(1)
            }
            now = now.withHour(alarmTime.hour).withMinute(alarmTime.minute) // .withSecond(alarmTime.second).withNano(alarmTime.nano)
            val duration = Duration.between(LocalDateTime.now(), now)

            Log.d(TAG, "runAt: ${duration.seconds}s")

            val data = workDataOf(PARAM_NAME to "Timer 01")

            val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(duration.seconds, TimeUnit.SECONDS)
                .setInputData(data) // optional
                .build()

            workManager.enqueueUniqueWork(REMINDER_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest)
        }

        fun cancel() {
            Log.d(TAG, "runAt:cancels")
            val workManager = WorkManager.getInstance()
            workManager.cancelUniqueWork(REMINDER_WORK_NAME)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result = coroutineScope {
        val worker = this@ReminderWorker
        val context = applicationContext
        val name = inputData.getString(PARAM_NAME)
        Log.d(TAG, "runAt:$name")

        var isScheduleNext = true
        try {
            Result.success()
        }
        catch (e: Exception) {
            // only retry 3 times
            if (runAttemptCount > 3) {
                Log.d(TAG, "runAttemptCount=$runAttemptCount, stop retry")
                return@coroutineScope Result.success()
            }

            // retry if network failure, else considered failed
            when(e.cause) {
                is SocketException -> {
                    Log.d(TAG, e.toString()+ e.message)
                    isScheduleNext = false
                    Result.retry()
                }
                else -> {
                    Log.d(TAG, e.toString())
                    Result.failure()
                }
            }
        }
        finally {
            // only schedule next day if not retry, else it will overwrite the retry attempt
            // - because we use uniqueName with ExistingWorkPolicy.REPLACE
            if (isScheduleNext) {
                runAt() // schedule for next day
            }
        }
    }
}
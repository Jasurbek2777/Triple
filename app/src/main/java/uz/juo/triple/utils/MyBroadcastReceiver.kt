package uz.juo.triple.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.media.MediaPlayer
import android.content.Intent
import android.util.Log
import uz.juo.triple.R
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import uz.juo.triple.MainActivity
import uz.juo.triple.room.AppDataBase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.os.Build




class MyBroadcastReceiver : BroadcastReceiver() {
    private val TAG = "MyBroadcastReceiver"
    var mp_id = 0

    override fun onReceive(context: Context, intent: Intent) {
        var dao = AppDataBase.getInstance(context).dao()
        var list = dao.getAll() as ArrayList
        if (list.isNotEmpty()) {
            showNotify(context, intent)
            val alarmManager1 = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
            val pendingIntent1 = PendingIntent.getBroadcast(
                context.applicationContext, 234, intent, 0
            )
            alarmManager1!![AlarmManager.RTC_WAKEUP, list[0].alarmSeconds] = pendingIntent1
            context.startService(Intent(context, NotificationService::class.java))
            Log.d(TAG, "Time12: ${list[0].hour} ${list[0].minut}")
            Log.d(TAG, "start: ${list[0].alarmSeconds}")
            dao.deleteAll()
            list.removeAt(0)
            for (i in list) {
                dao.add(i)
            }
        } else {
            Toast.makeText(context, "Alarm's are not found", Toast.LENGTH_SHORT).show()
            SharedPreference.getInstance(context).hasAlarm = false
        }

    }


    private fun showNotify(context: Context, intent: Intent) {
        var notifyReciver = Intent(context, NotificationReceiver::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, NotificationService::class.java))
        } else {
            context.startService(Intent(context, NotificationService::class.java))
        }
        notifyReciver.putExtra("id", mp_id)
        val pendingIntentN = PendingIntent.getBroadcast(
            context.applicationContext, 0, notifyReciver, PendingIntent.FLAG_UPDATE_CURRENT
        )
        Calendar.getInstance().timeInMillis
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date().time)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        var cIntent =
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0);
        val mBuilder = NotificationCompat.Builder(context, "id")
            .setContentTitle("Alarm")
            .setContentText(currentDate)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(R.drawable.book, "Cancel", pendingIntentN)
            .setSmallIcon(R.drawable.book)
            .setContentIntent(cIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
        val mNotificationManager = NotificationManagerCompat.from(context)
        mNotificationManager.notify(123, mBuilder.build())
    }

}
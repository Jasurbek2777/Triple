package uz.juo.triple.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import androidx.core.app.NotificationManagerCompat
import android.media.AudioManager
import android.view.KeyEvent
import uz.juo.triple.R


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        var not = NotificationManagerCompat.from(context)
        not.cancel(123)
        context.stopService(Intent(context, NotificationService::class.java))
    }
}
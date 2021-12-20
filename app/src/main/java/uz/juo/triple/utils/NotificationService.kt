package uz.juo.triple.utils

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.widget.Toast
import uz.juo.triple.R

class NotificationService : Service() {
    lateinit var mp: MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        mp = MediaPlayer.create(this, R.raw.azan)
        mp.isLooping = false
        mp.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mp.stop()
    }
}
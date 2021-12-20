package uz.juo.triple.utils

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("5e394515-dbb4-43d9-8cb1-a0083cf083cc")
    }
}
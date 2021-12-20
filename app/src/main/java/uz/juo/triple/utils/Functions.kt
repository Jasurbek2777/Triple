package uz.juo.triple.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Functions {
    fun getLocationName(context: Context, latitude: Double, longitude: Double): String {
        var name = "City"
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (null != addresses && addresses.isNotEmpty()) {
                if (addresses[0].locality == null) {
                    name = addresses[0].countryName
                } else {
                    name = addresses[0].locality
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return name
        }
        return name
    }

    fun weatherDataConvertor(date: Long): ArrayList<Long> {
        var rDays = date / 86400 % 30
        var year = (date / 31556926)
        var month = (date % 31556926) / 2629743 + 1
        var list = ArrayList<Long>(listOf(year + 1970, month, rDays))
        return list
    }

    fun getTime(str: String): ArrayList<String> {
        var list = ArrayList<String>()
        var hour = str.substring(0, 2)
        var minut = str.substring(3, 5)
        list.add(hour)
        list.add(minut)
        return list
    }
}
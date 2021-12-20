package uz.juo.triple.models.azon

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Timings(
    val Fajr: String,
    val Dhuhr: String,
    val Asr: String,
    val Maghrib: String,
    val Isha: String,
//    val Imsak: String,
//    val Midnight: String,
//    val Sunrise: String,
//    val Sunset: String
)
package uz.juo.triple.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "azon_db")

data class AzonEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var hour: Int,
    var date: String,
    var type: String,
    var minut: Int,
    var alarmSeconds:Long
)
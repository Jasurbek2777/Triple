package uz.juo.triple.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import uz.juo.triple.room.dao.AzonDao
import uz.juo.triple.room.entity.AzonEntity

@Database(entities = [AzonEntity::class], version = 3)
abstract class AppDataBase : RoomDatabase() {
    abstract fun dao(): AzonDao

    companion object {
        private var instance: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "AppDataBase"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}

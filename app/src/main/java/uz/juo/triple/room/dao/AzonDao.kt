package uz.juo.triple.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import uz.juo.triple.models.azon.AzonData
import uz.juo.triple.room.entity.AzonEntity

@Dao
interface AzonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(mediacament: AzonEntity)

    @Query("select * from azon_db where id=:id")
    fun getById(id: Int): AzonEntity

    @Query("select * from azon_db")
    fun getAll(): List<AzonEntity>

    @Query("Delete from azon_db where id=:id")
    fun delete(id: Int)

    @Query("Delete from azon_db ")
    fun deleteAll()
}
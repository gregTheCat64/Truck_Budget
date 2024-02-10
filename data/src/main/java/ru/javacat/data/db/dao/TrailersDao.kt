package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.javacat.data.db.entities.DbTrailer

@Dao
interface TrailersDao {

    @Query("SELECT * FROM trailers_table")
    fun getAll(): List<DbTrailer>

    @Query("SELECT * FROM trailers_table WHERE regNumber =:id")
    suspend fun getById(id: String): DbTrailer

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        dbVehicle: DbTrailer
    )

    @Update()
    suspend fun update(
        dbVehicle: DbTrailer
    )

    @Query("DELETE FROM trailers_table WHERE regNumber =:id")
    suspend fun remove(id: Int)
}
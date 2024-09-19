package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.javacat.data.db.entities.DbLocation

@Dao
interface LocationsDao {

    @Query("SELECT * FROM locations_table")
    suspend fun getLocations(): List<DbLocation>

    @Query("SELECT * FROM locations_table WHERE name LIKE '%' || :search || '%'")
    suspend fun searchLocations(search: String): List<DbLocation>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(
        location: DbLocation
    )

    @Query("DELETE FROM locations_table WHERE id = :id")
    suspend fun remove(id: Long)

}
package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.javacat.data.db.entities.DbTruckDriver

@Dao
interface TruckDriversDao {
    @Query("SELECT * FROM truck_drivers_table")
    fun getAll(): List<DbTruckDriver>

    @Query("SELECT * FROM truck_drivers_table WHERE passportNumber =:id")
    suspend fun getById(id: String): DbTruckDriver

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        staff: DbTruckDriver
    )

    @Query("SELECT * FROM truck_drivers_table " +
            "WHERE surName LIKE '%' || :search || '%'")
    suspend fun searchStaff(search: String): List<DbTruckDriver>

    @Update()
    suspend fun update(
        staff: DbTruckDriver
    )

    @Query("DELETE FROM truck_drivers_table WHERE passportNumber =:id")
    suspend fun removeStaff(id: Int)
}
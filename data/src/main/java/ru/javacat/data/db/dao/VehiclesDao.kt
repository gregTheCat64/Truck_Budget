package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbStaff
import ru.javacat.data.db.entities.DbVehicle
import ru.javacat.domain.models.Staff

@Dao
interface VehiclesDao {

    @Query("SELECT * FROM vehicles_table")
    fun getAll(): Flow<List<DbVehicle>>

    @Query("SELECT * FROM vehicles_table WHERE regNumber =:id")
    suspend fun getById(id: String): DbVehicle

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        dbVehicle: DbVehicle
    )

    @Update()
    suspend fun update(
        dbVehicle: DbVehicle
    )

    @Query("DELETE FROM vehicles_table WHERE regNumber =:id")
    suspend fun remove(id: Int)
}
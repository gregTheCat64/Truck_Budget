package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CargoDao {

    @Query("SELECT * FROM cargo_table")
    suspend fun getCargos(): List<DbCargo>

    @Query("SELECT * FROM cargo_table " +
            "WHERE name LIKE '%' || :search || '%'")
    suspend fun searchCargos(search: String): List<DbCargo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCargo(cargo: DbCargo)
}
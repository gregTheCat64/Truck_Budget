package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.javacat.data.db.entities.DbCargo
import ru.javacat.domain.models.Cargo

@Dao
interface CargoDao {

    @Query("SELECT * FROM cargo_table")
    suspend fun getCargos(): List<DbCargo>

    @Query("SELECT * FROM CARGO_TABLE WHERE id =:id")
    suspend fun getCargoById(id: Long): DbCargo

    @Query("SELECT * FROM cargo_table " +
            "WHERE name LIKE '%' || :search || '%'")
    suspend fun searchCargos(search: String): List<DbCargo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCargo(cargo: DbCargo): Long

    @Query("DELETE FROM cargo_table WHERE id = :id")
    suspend fun remove(id: Long)
}
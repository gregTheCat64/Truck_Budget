package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.javacat.data.db.entities.DbTruck


@Dao
interface TrucksDao {

    @Query("SELECT * FROM trucks_table WHERE isHidden = 0")
    fun getAll(): List<DbTruck>

    @Query("SELECT * FROM trucks_table WHERE id =:id")
    suspend fun getById(id: Long): DbTruck?

    @Query("SELECT * FROM trucks_table WHERE companyId =:id AND isHidden = 0")
    suspend fun getByCompanyId(id: Long): List<DbTruck>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        dbVehicle: DbTruck
    ): Long

    @Query("SELECT * FROM trucks_table " +
            "WHERE regNumber LIKE '%' || :search || '%'")
    suspend fun searchTrucks(search: String): List<DbTruck>

    @Update()
    suspend fun update(
        dbVehicle: DbTruck
    )

    @Query("DELETE FROM trucks_table WHERE id =:id")
    suspend fun remove(id: Long)
}
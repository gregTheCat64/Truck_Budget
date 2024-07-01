package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck

@Dao
interface TrailersDao {

    @Query("SELECT * FROM trailers_table")
    fun getAll(): List<DbTrailer>

    @Query("SELECT * FROM trailers_table WHERE id =:id")
    suspend fun getById(id: Long): DbTrailer

    @Query("SELECT * FROM trailers_table WHERE companyId =:id")
    suspend fun getByCompanyId(id: Long): List<DbTrailer>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        dbVehicle: DbTrailer
    )

    @Query("SELECT * FROM trailers_table " +
            "WHERE regNumber LIKE '%' || :search || '%'")
    suspend fun searchTrailers(search: String): List<DbTrailer>

    @Update()
    suspend fun update(
        dbVehicle: DbTrailer
    )

    @Query("DELETE FROM trailers_table WHERE id =:id")
    suspend fun remove(id: Long)
}
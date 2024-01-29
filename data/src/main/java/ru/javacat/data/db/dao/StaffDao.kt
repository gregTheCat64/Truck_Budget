package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbStaff

@Dao
interface StaffDao {
    @Query("SELECT * FROM staff_table")
    fun getAll(): Flow<List<DbStaff>>

    @Query("SELECT * FROM staff_table WHERE passportNumber =:id")
    suspend fun getById(id: String): DbStaff

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        staff: DbStaff
    )

    @Update()
    suspend fun update(
        staff: DbStaff
    )

    @Query("DELETE FROM staff_table WHERE passportNumber =:id")
    suspend fun removeStaff(id: Int)
}
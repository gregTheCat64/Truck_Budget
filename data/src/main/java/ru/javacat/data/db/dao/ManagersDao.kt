package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.javacat.data.db.entities.DbManager

@Dao
interface ManagersDao {

    @Query("SELECT * FROM managers_table")
    fun getAll(): List<DbManager>

    @Query("SELECT * FROM managers_table WHERE surName LIKE '%' || :search || '%'")
    suspend fun searchManagers(search: String): List<DbManager>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManager(
        employee: DbManager
    )

    @Query("SELECT * FROM managers_table WHERE customerId =:customerId")
    fun getManagersByCustomerId(
        customerId: Long
    ): List<DbManager>

    @Query("SELECT * FROM managers_table WHERE id =:id")
    fun getById(id: Long): DbManager

    @Query("DELETE FROM managers_table WHERE id =:id")
    suspend fun removeEmployee(id: Int)

    @Update()
    suspend fun updateEmployee(
        employee: DbManager
    )
}
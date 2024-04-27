package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.data.db.models.DbCustomerWithEmployees

@Dao
interface EmployeesDao {

    @Query("SELECT * FROM employees_table")
    fun getAll(): List<DbEmployee>

    @Query("SELECT * FROM employees_table WHERE name LIKE '%' || :search || '%'")
    suspend fun searchEmployees(search: String): List<DbEmployee>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(
        employee: DbEmployee
    )

    @Query("SELECT * FROM employees_table WHERE customerId =:customerId")
    fun getEmployeesByCustomerId(
        customerId: Long
    ): List<DbEmployee>

    @Query("SELECT * FROM employees_table WHERE id =:id")
    fun getById(id: Long): DbEmployee

    @Query("DELETE FROM employees_table WHERE id =:id")
    suspend fun removeEmployee(id: Int)

    @Update()
    suspend fun updateEmployee(
        employee: DbEmployee
    )
}
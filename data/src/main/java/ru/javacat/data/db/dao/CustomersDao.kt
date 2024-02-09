package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbEmployee
import ru.javacat.data.db.models.DbCustomerWithEmployees
import ru.javacat.domain.models.Employee

@Dao
interface CustomersDao {

    @Transaction
    @Query("SELECT * FROM customers_table")
    fun getAll(): Flow<List<DbCustomerWithEmployees>>


    @Transaction
    @Query("SELECT * FROM customers_table WHERE atiNumber =:id")
    suspend fun getById(id: String): DbCustomerWithEmployees


    @Query("SELECT * FROM customers_table")
    suspend fun getCustomers(): List<DbCustomer>

    @Query("SELECT * FROM customers_table WHERE companyName LIKE '%' || :search || '%'")
    suspend fun searchCustomers(search: String): List<DbCustomer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(
        customer: DbCustomer
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(
        employee: DbEmployee
    )

    @Query("SELECT * FROM employees_table WHERE customerId =:customerId")
    fun getEmployeesByCustomerId(
        customerId: String
    ): List<DbEmployee>

    @Update()
    suspend fun updateCustomer(
        customer: DbCustomer,
        employee: List<DbEmployee>
    )

    @Update()
    suspend fun updateEmployee(
        employee: DbEmployee
    )

    @Query("DELETE FROM customers_table WHERE atiNumber =:id")
    suspend fun removeCustomer(id: Int)

    @Query("DELETE FROM employees_table WHERE id =:id")
    suspend fun removeEmployee(id: Int)



    //TODO: ИЗМЕНИТЬ вставку работников отдельно от клиента
    //и заказов от поездки
    //и поинты
}
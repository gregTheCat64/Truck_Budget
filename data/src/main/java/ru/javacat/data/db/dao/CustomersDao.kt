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
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.models.CustomersWithEmployees
import ru.javacat.data.db.models.RouteWithOrders

@Dao
interface CustomersDao {

    @Query("SELECT * FROM customers_table")
    fun getAll(): Flow<List<CustomersWithEmployees>>

    @Query("SELECT * FROM customers_table WHERE atiNumber =:id")
    suspend fun getById(id: String): CustomersWithEmployees


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(
        customer: DbCustomer
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(
        employee: DbEmployee
    )

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
package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbManager
import ru.javacat.data.db.models.DbCustomerWithManagers

@Dao
interface CustomersDao {

    @Transaction
    @Query("SELECT * FROM customers_table")
    fun getAll(): List<DbCustomerWithManagers>

    @Transaction
    @Query("SELECT * FROM customers_table WHERE id =:id")
    suspend fun getById(id: Long): DbCustomerWithManagers?

    @Query("SELECT * FROM customers_table")
    suspend fun getCustomers(): List<DbCustomerWithManagers>

    @Query("SELECT * FROM customers_table WHERE companyName LIKE '%' || :search || '%'")
    suspend fun searchCustomers(search: String): List<DbCustomerWithManagers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(
        customer: DbCustomer
    )

    @Update()
    suspend fun updateCustomer(
        customer: DbCustomer,
        employee: List<DbManager>
    )



    @Query("DELETE FROM customers_table WHERE atiNumber =:id")
    suspend fun removeCustomer(id: Int)





    //TODO: ИЗМЕНИТЬ вставку работников отдельно от клиента
    //и заказов от поездки
    //и поинты
}
package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.models.DbOrderWithCustomer

@Dao
interface OrdersDao {
    @Transaction
    @Query("SELECT * FROM orders_table")
    fun getAllOrders(): List<DbOrderWithCustomer>

    @Transaction
    @Query("SELECT * FROM orders_table WHERE strftime('%Y', date) = :year")
    fun getAllOrdersByYear(year: String): List<DbOrderWithCustomer>

    @Transaction
    @Query("SELECT * FROM orders_table WHERE isPaidByCustomer = 0")
    suspend fun getUnpaidOrder(): List<DbOrderWithCustomer>

    @Transaction
    @Query("SELECT * FROM orders_table  ORDER BY id DESC LIMIT 1")
    fun getLastOrder(): DbOrderWithCustomer?

    @Query("""
        SELECT COUNT(*) FROM orders_table 
        WHERE   contractorId = -1 AND
            strftime('%Y', date) = :year
    """)
    fun getCountCompanyOrdersByYear(year: String): Int

    @Query("""
        SELECT COUNT(*) FROM orders_table 
        WHERE   contractorId != -1 AND
            strftime('%Y', date) = :year
    """)
    fun getCountNotCompanyOrdersByYear(year: String): Int


    @Upsert
    suspend fun insertOrder(
        order: DbOrder,
        //points: List<DbPoint>
    ): Long

    @Transaction
    @Query("SELECT * FROM orders_table WHERE id =:id")
    suspend fun getByOrderId(id: Long): DbOrderWithCustomer

    @Update
    suspend fun updateOrder(
        order: DbOrder
    )

    @Query("DELETE FROM orders_table WHERE id =:id")
    suspend fun removeOrder(id: Long)
}
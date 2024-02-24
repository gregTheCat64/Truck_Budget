package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.models.DbOrderWithPointsAndCustomer
import ru.javacat.data.db.models.DbRouteWithOrders

@Dao
interface RoutesDao {

    @Transaction
    @Query("SELECT * FROM routes_table")
    fun getAllRoutes(): Flow<List<DbRouteWithOrders>>

    @Transaction
    @Query("SELECT * FROM orders_table")
    fun getAllOrders(): Flow<List<DbOrderWithPointsAndCustomer>>

    @Transaction
    @Query("SELECT * FROM routes_table WHERE id =:id")
    suspend fun getByRouteId(id: Long): DbRouteWithOrders

    @Transaction
    @Query("SELECT * FROM orders_table WHERE id =:id")
    suspend fun getByOrderId(id: String): DbOrderWithPointsAndCustomer

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(
        route: DbRoute
    ): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(
        order: DbOrder,
        points: List<DbPoint>
    )


    @Update
    suspend fun updateRoute(
        route: DbRoute
    )
    @Update
    suspend fun updateOrder(
        order: DbOrder
    )

    @Query("DELETE FROM routes_table WHERE id =:id")
    suspend fun removeRoute(id: Long)

    @Query("DELETE FROM orders_table WHERE id =:id")
    suspend fun removeOrder(id: String)
}
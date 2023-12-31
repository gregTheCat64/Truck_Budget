package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbLocation
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.models.OrderWithPointsAndCustomer
import ru.javacat.data.db.models.RouteWithOrders

@Dao
interface RoutesDao {

    @Transaction
    @Query("SELECT * FROM routes_table")
    fun getAllRoutes(): Flow<List<RouteWithOrders>>

    @Transaction
    @Query("SELECT * FROM orders_table")
    fun getAllOrders(): Flow<List<OrderWithPointsAndCustomer>>

    @Transaction
    @Query("SELECT * FROM routes_table WHERE id =:id")
    suspend fun getByRouteId(id: String): RouteWithOrders

    @Transaction
    @Query("SELECT * FROM orders_table WHERE id =:id")
    suspend fun getByOrderId(id: String): OrderWithPointsAndCustomer

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(
        route: DbRoute
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(
        order: DbOrder,
        points: List<DbPoint>
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(
        location: DbLocation
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
    suspend fun removeRoute(id: String)

    @Query("DELETE FROM orders_table WHERE id =:id")
    suspend fun removeOrder(id: String)
}
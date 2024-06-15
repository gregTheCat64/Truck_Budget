package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbCountRoute

import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.models.DbRouteWithOrders

@Dao
interface RoutesDao {

    @Transaction
    @Query("SELECT * FROM routes_table")
    fun getAllRoutes(): List<DbRouteWithOrders>

    @Transaction
    @Query("SELECT * FROM routes_table WHERE isFinished = 1 ORDER BY id DESC LIMIT 1 ")
    fun getLastRoute(): DbRouteWithOrders?

    @Transaction
    @Query("SELECT * FROM routes_table WHERE id =:id")
    suspend fun getByRouteId(id: Long): DbRouteWithOrders?


    @Upsert
    suspend fun insertRoute(
        route: DbRoute
    ): Long

    @Upsert
    suspend fun insertCountRoute(
        countRoute: DbCountRoute
    )

    @Update
    suspend fun updateRoute(
        route: DbRoute
    )

    @Update
    suspend fun updateCountRoute(countRoute: DbCountRoute)


    @Query("DELETE FROM routes_table WHERE id =:id")
    suspend fun removeRoute(id: Long)


}
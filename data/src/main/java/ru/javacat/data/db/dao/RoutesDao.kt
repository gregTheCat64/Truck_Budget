package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.models.RouteWithOrders

@Dao
interface RoutesDao {

    @Query("SELECT * FROM routes_table")
    fun getAll(): Flow<List<RouteWithOrders>>

    @Query("SELECT * FROM routes_table WHERE id =:id")
    suspend fun getById(id: String): RouteWithOrders

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(
        route: DbRoute,
        order: DbOrder
    )

    @Update()
    suspend fun update(
        route: DbRoute,
        order: DbOrder
    )

}
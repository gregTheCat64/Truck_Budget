package ru.javacat.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.models.DbMonthlyProfit
import ru.javacat.data.db.models.DbRouteWithOrders

@Dao
interface RoutesDao {

    @Transaction
    @Query("SELECT * FROM routes_table")
    fun getAllRoutes(): List<DbRouteWithOrders>

    @Transaction
    @Query("SELECT * FROM routes_table WHERE strftime('%Y', startDate) = :year ORDER BY id DESC")
    fun getAllRoutesByYear(year: String): List<DbRouteWithOrders>


    @Query("SELECT * FROM routes_table WHERE contractorId = -1 AND strftime('%Y', startDate) = :year ORDER BY id DESC")
    fun getCompanyRoutes(year: String): List<DbRouteWithOrders>

    @Query(
        """
        SELECT COUNT(*) FROM routes_table 
        WHERE   contractorId = -1 AND
            strftime('%Y', startDate) = :year
    """
    )
    fun getCompanyRoutesCount(year: String): Int

    @Query(
        """
        SELECT COUNT(*) FROM routes_table 
        WHERE   contractorId != -1 AND
            strftime('%Y', startDate) = :year
    """
    )
    fun getNotCompanyRoutesCount(year: String): Int

    @Transaction
    @Query("SELECT * FROM routes_table WHERE contractorId = -1 AND isFinished = 1 ORDER BY id DESC LIMIT 1 ")
    fun getLastRoute(): DbRouteWithOrders?

    @Transaction
    @Query("SELECT * FROM routes_table WHERE id =:id")
    suspend fun getByRouteId(id: Long): DbRouteWithOrders?


    @Upsert
    suspend fun insertRoute(
        route: DbRoute
    ): Long

//    @Upsert
//    suspend fun insertCountRoute(
//        countRoute: DbCountRoute
//    )

    @Update
    suspend fun updateRoute(
        route: DbRoute
    )

//    @Update
//    suspend fun updateCountRoute(countRoute: DbCountRoute)


    @Query("DELETE FROM routes_table WHERE id =:id")
    suspend fun removeRoute(id: Long)


    @Query(
        """
        SELECT strftime('%m', startDate) as monthDate,
        SUM(profit) as totalProfit
        FROM routes_table
        WHERE strftime('%Y', startDate) = :year
        AND contractorId = -1
        GROUP BY monthDate
        ORDER BY monthDate
    """
    )
    fun getMonthlyIncomeByYear(year: String): List<DbMonthlyProfit>

    @Query(
        """
        SELECT strftime('%m', startDate) as monthDate,
        SUM(profit) as totalProfit
        FROM routes_table
        WHERE strftime('%Y', startDate) = :year
        AND contractorId != -1
        GROUP BY monthDate
        ORDER BY monthDate
    """
    )
    fun getMonthlyIncomeByYearFromNotCompanyTransport(year: String): List<DbMonthlyProfit>
}
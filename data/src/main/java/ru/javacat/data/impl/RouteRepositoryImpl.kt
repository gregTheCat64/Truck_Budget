package ru.javacat.data.impl

import android.content.Context
import android.util.Log
import androidx.room.withTransaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.AppDb
import ru.javacat.data.db.dao.RoutesDao

import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.MonthlyProfit

import ru.javacat.domain.models.Route
import ru.javacat.domain.models.SalaryParameters
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class RouteRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val routesDao: RoutesDao,
    private val db: AppDb
):RouteRepository {
//    override val allRoutes: Flow<List<Route?>>
//        get() = routesDao.getAllRoutes().map { list -> list.map { it.toRouteModel() } }

    private val TAG = "RouteRepoImpl"
    override val lastRoute: Route?
        get() = routesDao.getLastRoute()?.toRouteModel()

    private var _routes = MutableStateFlow(emptyList<Route>())
    override val items: Flow<List<Route>>
        get() = _routes

    private val _editedRoute = MutableStateFlow(Route())
    override val editedItem: StateFlow<Route?>
        get() = _editedRoute.asStateFlow()

    private val _isEdited = MutableStateFlow(false)
    override val isEdited: StateFlow<Boolean>
        get() = _isEdited.asStateFlow()

    override suspend fun getAll() {
        _routes.emit(routesDao.getAllRoutes().map {it.toRouteModel() })
    }

    override suspend fun getAllByYear(year: Int) {
        _routes.emit(routesDao.getAllRoutesByYear(year.toString()).map {it.toRouteModel() })
    }

    override suspend fun getCompanyRoutes(year: Int) {
        _routes.emit(routesDao.getCompanyRoutes(year.toString()).map { it.toRouteModel() })
    }

    override suspend fun getById(id: Long): Route? = routesDao.getByRouteId(id)?.toRouteModel()

    override suspend fun getMonthlyIncomeByYear(year: String): List<MonthlyProfit> {
        //println("routesdaoRes : ${routesDao.getMonthlyIncomeByYear(year)}")
        return dbQuery { routesDao.getMonthlyIncomeByYear(year).map{
            it.toMonthlyProfitModel()
        } }
    }

    override suspend fun getMonthlyIncomeByYearNotCompanyTransport(year: String): List<MonthlyProfit> {
        return dbQuery { routesDao.getMonthlyIncomeByYearFromNotCompanyTransport(year).map{
            it.toMonthlyProfitModel()
        } }
    }

    override suspend fun getCompanyRoutesCountByYear(year: String): Int {
        return dbQuery { routesDao.getCompanyRoutesCount(year) }
    }

    override suspend fun getNotCompanyRoutesCountByYear(year: String): Int {
        return dbQuery { routesDao.getNotCompanyRoutesCount(year) }
    }

    override suspend fun updateEditedItem(t: Route) {
        _editedRoute.emit(t)
        _isEdited.emit(true)
        Log.i("RouteRepo", "edited Route: ${editedItem.value}")
    }

    override suspend fun removeById(id: Long) {
        dbQuery {
            switchDatabaseModified(context, true)
            routesDao.removeRoute(id) }
    }

    override suspend fun insert(route: Route): Long {
        println("inserting in repo $route")
        //_isEdited.emit(false)
        var routeId = 0L

        db.withTransaction {
            routeId = dbQuery {
                routesDao.insertRoute(route.toDb())
            }
            Log.i("RouteRepo", "routeId : $routeId")
        }
        switchDatabaseModified(context, true)
        return routeId
    }

    override suspend fun updateRouteToDb(route: Route){
        Log.i(TAG, "saving route: ${route.id}")
        dbQuery {
            switchDatabaseModified(context, true)
            routesDao.updateRoute(route.toDb()) }
    }
}
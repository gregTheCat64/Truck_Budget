package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.Staff
import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.RouteRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

val draftRoute = Route()

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
):RouteRepository {
    override val allRoutes: Flow<List<Route?>>
        get() = routesDao.getAllRoutes().map { list -> list.map { it.toRouteModel() } }

    override val lastRouteId: Long?
        get() = routesDao.getLastRoute()?.route?.id

    private val _editedRoute = MutableStateFlow<Route>(draftRoute)
    override val editedRoute: StateFlow<Route>
        get() = _editedRoute.asStateFlow()

    override suspend fun getRoute(id: Long): Route? = routesDao.getByRouteId(id)?.toRouteModel()

    override suspend fun updateRoute(newRoute: Route) {
        Log.i("RouteRepo", "new Route: ${newRoute}")
        _editedRoute.emit(newRoute)
        Log.i("RouteRepo", "edited Route: ${editedRoute.value}")
    }

    override suspend fun removeRoute(id: Long) {
        dbQuery { routesDao.removeRoute(id) }
    }


    override suspend fun insertRoute(route: Route): Long {
        println("inserting in repo $route")
        val result = dbQuery { routesDao.insertRoute(route.toDb()) }
        //_editedRoute.emit(route)
        return result

    }


}
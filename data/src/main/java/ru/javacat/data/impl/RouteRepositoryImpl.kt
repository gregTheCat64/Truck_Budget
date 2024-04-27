package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject
import javax.inject.Singleton

val draftRoute = Route(

)

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
):RouteRepository {
//    override val allRoutes: Flow<List<Route?>>
//        get() = routesDao.getAllRoutes().map { list -> list.map { it.toRouteModel() } }

    override val lastRoute: Route?
        get() = routesDao.getLastRoute()?.toRouteModel()

    private var _routes = MutableStateFlow(emptyList<Route>())
    override val routes: Flow<List<Route>>
        get() = _routes

    private val _editedRoute = MutableStateFlow(Route())
    override val editedRoute: StateFlow<Route>
        get() = _editedRoute.asStateFlow()

    private val _isEdited = MutableStateFlow(false)
    override val isEdited: StateFlow<Boolean>
        get() = _isEdited.asStateFlow()

    override suspend fun getAllRoutes() {
        _routes.emit(routesDao.getAllRoutes().map {it.toRouteModel() })
    }

    override suspend fun getRoute(id: Long): Route? = routesDao.getByRouteId(id)?.toRouteModel()

    override suspend fun updateEditedRoute(newRoute: Route) {
        _editedRoute.emit(newRoute)
        _isEdited.emit(true)
        Log.i("RouteRepo", "edited Route: ${editedRoute.value}")
    }

    override suspend fun removeRoute(id: Long) {
        dbQuery { routesDao.removeRoute(id) }
    }

    override suspend fun insertRoute(route: Route): Long {
        println("inserting in repo $route")
        _isEdited.emit(false)
        val result = dbQuery { routesDao.insertRoute(route.toDb()) }
        //_editedRoute.emit(route)
        return result
    }
}
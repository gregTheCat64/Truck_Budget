package ru.javacat.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.dao.VehiclesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.DraftRoute
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

val draftRoute = DraftRoute(
    null, null, null, null, null, null, null, null,
    null, null, null, null, null, null,
    null, null
)

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
):RouteRepository {
    override val allRoutes: Flow<List<Route?>>
        get() = routesDao.getAllRoutes().map { list -> list.map { it.toRouteModel() } }

    private val _editedRoute = MutableStateFlow<DraftRoute>(draftRoute)
    override val editedRoute: StateFlow<DraftRoute>
        get() = _editedRoute.asStateFlow()

    override suspend fun getRoute(id: String) = routesDao.getByRouteId(id).toRouteModel()

    override suspend fun updateRoute(newRoute: DraftRoute) {
        _editedRoute.emit(newRoute)
    }

    override suspend fun deleteRoute(route: Route) {
        TODO("Not yet implemented")
    }


    override suspend fun insertRoute(route: Route) {
        dbQuery { routesDao.insertRoute(route.toDb()) }
    }





}
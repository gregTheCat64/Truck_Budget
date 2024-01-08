package ru.javacat.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.CustomersDao
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.dao.StaffDao
import ru.javacat.data.db.dao.VehiclesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.Staff
import ru.javacat.domain.models.Vehicle
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
):RouteRepository {
    override val allRoutes: Flow<List<Route?>>
        get() = routesDao.getAllRoutes().map { list -> list.map { it.toRouteModel() } }

    override val allOrders: Flow<List<Order?>>
        get() = routesDao.getAllOrders().map {list-> list.map { it.toOrderModel() } }

    override suspend fun insertRoute(route: Route) {
        dbQuery { routesDao.insertRoute(route.toDb()) }
    }

    override suspend fun insertOrder(route:Route, order: Order) {
        dbQuery { routesDao.insertOrder(order.toDb(route), order.points.map { it.toDb(order) }) }
    }





}
package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route

interface RouteRepository {
    val allRoutes: Flow<List<Route?>>
    val allOrders: Flow<List<Order?>>

    suspend fun insertOrder(route:Route, order: Order)

    suspend fun insertRoute(route: Route)

}
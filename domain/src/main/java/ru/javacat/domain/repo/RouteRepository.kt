package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Route

interface RouteRepository {
    val allRoutes: Flow<List<Route?>>

    val editedRoute: StateFlow<Route>


    suspend fun getRoute(id: String): Route
    suspend fun updateRoute(newRoute: Route)
    suspend fun deleteRoute(route: Route)

    suspend fun insertRoute(route: Route)


}
package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.DraftRoute
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import java.time.LocalDate

interface RouteRepository {
    val allRoutes: Flow<List<Route?>>

    val editedRoute: StateFlow<DraftRoute>


    suspend fun getRoute(id: String):Route
    suspend fun updateRoute(newRoute: DraftRoute)
    suspend fun deleteRoute(route:Route)

    suspend fun insertRoute(route: Route)


}
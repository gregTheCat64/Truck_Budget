package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.CountRoute
import ru.javacat.domain.models.Route

interface RouteRepository: BaseRepository<Route, Long> {

    val lastRoute: Route?
    suspend fun updateRouteToDb(route: Route)


}
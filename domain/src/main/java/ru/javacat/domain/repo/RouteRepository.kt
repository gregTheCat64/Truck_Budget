package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Route

interface RouteRepository: BaseRepository<Route, Long> {
    //val allRoutes: Flow<List<Route?>>

    //val routes: Flow<List<Route>>

    val lastRoute: Route?

    //val editedRoute: StateFlow<Route>

    //val isEdited: StateFlow<Boolean>


//    suspend fun getAllRoutes()
//
//    suspend fun getRoute(id: Long): Route?
    //suspend fun updateEditedRoute(newRoute: Route)
//    suspend fun removeRoute(id: Long)
//
//    suspend fun insertRoute(route: Route): Long


}
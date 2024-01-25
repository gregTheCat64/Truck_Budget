package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val repo: RouteRepository
): ViewModel() {
    fun getAllRoutes()= repo.allRoutes


    suspend fun insertNewRoute(route: Route){
        repo.insertRoute(route)
    }

}
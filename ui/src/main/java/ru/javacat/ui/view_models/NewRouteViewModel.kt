package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import ru.javacat.domain.models.DraftRoute
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewRouteViewModel @Inject constructor(
    private val repository: RouteRepository
):ViewModel() {
    private val draftRoute = repository.editedRoute

    suspend fun editAndSaveRoute(id: String, driverId: String, truckId: String, trailerId: String? = null, prepayment: Int){
        var editedRoute = repository.getRoute(id)
        editedRoute = editedRoute.copy(id = id, driverId = driverId, truckId = truckId, trailerId = trailerId, prepayment = prepayment)
        repository.insertRoute(editedRoute)
    }

    suspend fun addDataToNewRoute(id: String, driverId: String, truckId: String, trailerId: String? = null, prepayment: Int){
        repository.updateRoute(draftRoute.value.copy(
            id = id, driverId = driverId, truckId = truckId, trailerId = trailerId, prepayment = prepayment
        ))
    }
}
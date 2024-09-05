package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.domain.models.RouteDetails
import ru.javacat.domain.repo.RouteRepository
import javax.inject.Inject

@HiltViewModel
class RouteCountViewModel @Inject constructor(
    private val routeRepository: RouteRepository
): ViewModel() {
    val editedRoute = routeRepository.editedItem

    fun setPaid(isPaid: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            val route = editedRoute.value?.copy(isPaidToContractor = isPaid)
            if (route != null) {
                routeRepository.updateRouteToDb(route)
            }
        }

    }
}
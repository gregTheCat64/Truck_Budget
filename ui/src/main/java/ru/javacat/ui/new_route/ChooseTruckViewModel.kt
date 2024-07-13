package ru.javacat.ui.new_route

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.use_case.GetTrucksByCompanyIdUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class ChooseTruckViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val getTrucksUseCase: GetTrucksByCompanyIdUseCase,
): ViewModel() {
    val editedRoute = routeRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _trucks = MutableStateFlow<List<Truck>?>(null)
    val trucks = _trucks.asStateFlow()

    fun getTrucks(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTrucksUseCase.invoke(id)
            _trucks.emit(result)
        }
    }

    fun setTruck(t: Truck) {
        viewModelScope.launch {
            editedRoute.value?.let {
                routeRepository.updateEditedItem(
                    it.copy(
                        contractor = it.contractor?.copy(
                            truck = t
                        )
                    )
                )
            }
        }
    }

    //    fun searchTrucks(s: String){
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = trucksRepository.search(s)
//            _trucks.emit(result)
//        }
//    }
}
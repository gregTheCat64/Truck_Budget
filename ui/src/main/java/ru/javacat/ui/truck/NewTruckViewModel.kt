package ru.javacat.ui.truck

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TrucksRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewTruckViewModel @Inject constructor(
    private val trucksRepository: TrucksRepository,
    private val routeRepository: RouteRepository
): ViewModel() {

    val editedRoute = routeRepository.editedItem
    val editedTruck = MutableStateFlow<Truck?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun getTruckById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val truck = trucksRepository.getById(id)
                editedTruck.emit(truck)
                Log.i("newTransportVm", "result: $truck")
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun insertNewTruck(truck: Truck, isNeedToSet: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val newTruckId = trucksRepository.insert(truck)
                val newTruck = truck.copy(id = newTruckId)
                if (isNeedToSet){
                    //setTruckUseCase.invoke(newTruck)
                    editedRoute.value?.let {
                        routeRepository.updateEditedItem(
                            it.copy(
                                contractor = it.contractor?.copy(
                                    truck = newTruck
                                )
                            )
                        )
                    }
                }
                _loadState.emit(LoadState.Success.Created)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }




}
package ru.javacat.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.use_case.SetTruckDriverUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewDriverViewModel @Inject constructor(
    private val truckDriversRepository: TruckDriversRepository,
    private val routeRepository: RouteRepository,
    private val setTruckDriverUseCase: SetTruckDriverUseCase
):ViewModel() {

    val editedTruckDriver = MutableStateFlow<TruckDriver?>(null)
    val editedRoute = routeRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

     fun insertNewDriver(driver: TruckDriver, isNeedToSet: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                val newDriverId = truckDriversRepository.insert(driver)
                val newDriver = driver.copy(id = newDriverId)
                Log.i("NewDriverVM", "newdriver: $newDriver")
                if (isNeedToSet){
                    //setTruckDriverUseCase.invoke(newDriver)
                    editedRoute.value?.let {
                        routeRepository.updateEditedItem(
                            it.copy(
                                contractor = it.contractor?.copy(
                                    driver = newDriver
                                )
                            )
                        )
                    }

                }
                _loadState.emit(LoadState.Success.Created)
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun getTruckDriverById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedTruckDriver.emit(truckDriversRepository.getById(id))
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun removeDriverById(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                 truckDriversRepository.removeById(id)
                Log.i("NewDriverVM", "deleting driver: $id")
                _loadState.emit(LoadState.Success.Removed)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}
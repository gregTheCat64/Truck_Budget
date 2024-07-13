package ru.javacat.ui.new_route

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.domain.repo.TruckDriversRepository
import ru.javacat.domain.use_case.GetTruckDriversByCompanyIdUseCase
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class ChooseTruckDriverViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val truckDriversRepository: TruckDriversRepository,
    private val getTruckDriversUseCase: GetTruckDriversByCompanyIdUseCase,
) : ViewModel() {
    val editedRoute = routeRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _drivers = MutableStateFlow<List<TruckDriver>?>(null)
    val drivers = _drivers.asStateFlow()

    fun getDriver(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getTruckDriversUseCase.invoke(id)
            _drivers.emit(result)
        }
    }

    fun setDriver(t: TruckDriver) {
        viewModelScope.launch {
            editedRoute.value?.let {
                routeRepository.updateEditedItem(
                    it.copy(
                        contractor = it.contractor?.copy(
                            driver = t
                        )
                    )
                )
            }
        }
    }

    suspend fun insertNewDriver(driver: TruckDriver) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newDriverId = truckDriversRepository.insert(driver)
                val newDriver = driver.copy(id = newDriverId)
                Log.i("NewDriverVM", "newdriver: $newDriver")

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


                _loadState.emit(LoadState.Success.Created)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    //    fun searchStaff(s: String){
//        viewModelScope.launch(Dispatchers.IO) {
//            val result = truckDriversRepository.search(s)
//            _drivers.emit(result)
//        }
//    }
}
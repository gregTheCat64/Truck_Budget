package ru.javacat.ui.edit_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.CargoName
import ru.javacat.domain.repo.CargoRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class EditCargoDialogViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cargoRepository: CargoRepository
): ViewModel() {
    val editedOrder = orderRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _cargoList = MutableStateFlow<List<CargoName>?>(null)
    val cargoList = _cargoList.asStateFlow()

    fun getCargos(){
        viewModelScope.launch(Dispatchers.IO){
            val result = cargoRepository.getAll()
            _cargoList.emit(result)
        }
    }

    fun searchCargos(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = cargoRepository.search(search)
            println("cargos: $result")
            _cargoList.emit(result)
        }
    }

    fun insertNewCargo(cargo: CargoName){
        viewModelScope.launch(Dispatchers.IO){
            cargoRepository.insert(cargo)
        }
    }

    fun removeCargo(cargoId: Long){
        viewModelScope.launch(Dispatchers.IO){
            cargoRepository.removeById(id = cargoId)
            val result = cargoRepository.getAll()
            _cargoList.emit(result)
        }
    }

    fun addCargoToOrder(cargoName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.value?.let {
                        orderRepository.updateEditedItem(
                            it.copy(
                                cargo = it.cargo?.copy(
                                    cargoName = cargoName
                                )?:Cargo(
                                    cargoName = cargoName,
                                    cargoWeight = null,
                                    cargoVolume = null,
                                    isBackLoad = true,
                                    isSideLoad = false,
                                    isTopLoad = false
                                    )
                            )
                        )
                }
                _loadState.emit(LoadState.Success.GoForward)
            } catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }
}
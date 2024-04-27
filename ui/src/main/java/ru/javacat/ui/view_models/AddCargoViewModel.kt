package ru.javacat.ui.view_models

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
class AddCargoViewModel @Inject constructor(
    private val cargoRepository: CargoRepository,
    private val orderRepository: OrderRepository
): ViewModel() {
    val editedOrder = orderRepository.editedOrder

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _cargo = MutableStateFlow<List<CargoName>?>(null)
    val cargo = _cargo.asStateFlow()

    fun getCargos(){
        viewModelScope.launch(Dispatchers.IO){
            val result = cargoRepository.getAll()
            _cargo.emit(result)
        }
    }

    fun searchCargos(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = cargoRepository.search(search)
            println("cargos: $result")
            _cargo.emit(result)
        }
    }

    fun insertNewCargo(cargo: CargoName){
        viewModelScope.launch(Dispatchers.IO){
            cargoRepository.insert(cargo)
        }
    }

    fun addCargoToOrder(cargo: Cargo) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.value?.let {
                    orderRepository.updateOrder(
                        it.copy(
                            cargo = cargo
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
package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class AddPaymentViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val orderRepository: OrderRepository
): ViewModel() {

    val editedOrder = orderRepository.editedItem
    val editedRoute = routeRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val newOrderIdFlow = MutableStateFlow<Long?>(null)

    fun addPaymentToOrder(price: Int, daysToPay: Int?, contractorPrice: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.value.copy(price = price, daysToPay = daysToPay, contractorPrice = contractorPrice)
                    .let { orderRepository.updateEditedItem(it) }
                val newOrderId = orderRepository.insert(editedOrder.value)
                newOrderIdFlow.emit(newOrderId)
                _loadState.emit(LoadState.Success.GoForward)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun getRouteById(id: Long): Route?{
        return routeRepository.getById(id)
    }
}
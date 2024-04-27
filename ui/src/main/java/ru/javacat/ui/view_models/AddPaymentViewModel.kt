package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Order
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import java.lang.Error
import javax.inject.Inject

@HiltViewModel
class AddPaymentViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val orderRepository: OrderRepository
): ViewModel() {

    val editedOrder = orderRepository.editedOrder
    val editedRoute = routeRepository.editedRoute

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    //val orderList = editedRoute.value.orderList.toMutableList()

    fun addPaymentToOrder(price: Int, daysToPay: Int?) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.value?.copy(price = price, daysToPay = daysToPay)
                    ?.let { orderRepository.updateOrder(it) }
                _loadState.emit(LoadState.Success.GoForward)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}
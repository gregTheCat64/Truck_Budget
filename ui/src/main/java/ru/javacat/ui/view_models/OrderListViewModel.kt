package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import java.time.Month
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val orderRepository: OrderRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()
    val orders = orderRepository.orders

    fun getAllOrders(){
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getAllOrders()
        }
    }

    fun getUnpaidOrders(){
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getUnpaidOrders()
        }
    }

    fun getOrdersByMonth(month: Month){
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getOrdersByMonth(month)
        }
    }

    suspend fun getOrderAndUpdateEditedOrder(id: Long) {
        _loadState.emit(LoadState.Loading)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //saveRoute()
                val editedOrder = orderRepository.getOrderById(id)
                orderRepository.restoringOrder(editedOrder)
                _loadState.emit(LoadState.Success.GoForward)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}
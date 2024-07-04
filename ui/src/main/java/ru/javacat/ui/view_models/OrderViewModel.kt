package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Order
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
):ViewModel() {

    var editedOrder = MutableStateFlow<Order?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    fun getOrderById(id: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                editedOrder.emit(orderRepository.getById(id))
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun updateOrder(
        paymentDeadline: LocalDate? = null,
        sentDocsNumber: String? = null,
        docsReceived: LocalDate? = null,
        isPaid: Boolean? = null
    ){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                val orderToUpdate = editedOrder.value?.copy(
                    paymentDeadline = paymentDeadline?: editedOrder.value!!.paymentDeadline,
                    sentDocsNumber = sentDocsNumber?: editedOrder.value!!.sentDocsNumber,
                    docsReceived = docsReceived?: editedOrder.value!!.docsReceived,
                    isPaidByCustomer = isPaid?:false
                )
                orderToUpdate?.let { orderRepository.updateOrder(it) }
                _loadState.emit(LoadState.Success.OK)
            } catch (e:Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

}
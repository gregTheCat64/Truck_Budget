package ru.javacat.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val orderRepository: OrderRepository
):ViewModel() {
    val editedOrder = orderRepository.editedOrder
    val editedRoute = routeRepository.editedRoute
    val isOrderEdited = orderRepository.isOrderEdited

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    //private val orderList = editedRoute.value.orderList.toMutableList()

    init {
        //setOrderFlag(false)
    }

    fun saveOrder(order: Order){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                orderRepository.insertOrder(order)
                //updateEditedRoute(order)
                //orderList.add(order)
                val orders = routeRepository.getRoute(editedRoute.value.id?:0)?.orderList
                val routeToUpdate = editedRoute.value.copy(
                    orderList = orders?: emptyList(),
                    startDate = order.points[0].arrivalDate
                )

                routeRepository.insertRoute(routeToUpdate)
                routeRepository.updateEditedRoute(routeToUpdate)
                orderRepository.clearCurrentOrder()

                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

//    fun editPrice(price: Int){
//        viewModelScope.launch(Dispatchers.IO) {
//            orderRepository.updateOrder(editedOrder.value.copy(price = price))
//        }
//    }
//
//    fun editDaysToPay(days: Int){
//        viewModelScope.launch(Dispatchers.IO) {
//            orderRepository.updateOrder(editedOrder.value.copy(daysToPay = days))
//        }
//    }
//
//    fun editStatus(status: OrderStatus){
//        viewModelScope.launch(Dispatchers.IO){
//            orderRepository.updateOrder(editedOrder.value.copy(status = status))
//        }
//    }

    fun setOrderFlag(isEdited: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.setOrderFlag(isEdited)
        }
    }

    fun editOrder(
        price: Int? = null,
        cargo: Cargo? = null,
        daysToPay: Int? = null,
        paymentDeadline: LocalDate? = null,
        sentDocsNumber: String? = null,
        docsReceived: LocalDate? = null,
        isPaid: Boolean? = null
        ){
        Log.i("orderVM", "editOrder")
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.updateOrder(editedOrder.value.copy(
                price = price?:editedOrder.value.price,
                cargo = cargo?:editedOrder.value.cargo,
                daysToPay = daysToPay?:editedOrder.value.daysToPay,
                paymentDeadline = paymentDeadline?:editedOrder.value.paymentDeadline,
                sentDocsNumber = sentDocsNumber?:editedOrder.value.sentDocsNumber,
                docsReceived = docsReceived?:editedOrder.value.docsReceived,
                isPaid = isPaid?:false
            ))
        }
    }

    fun clearOrder(){
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.clearCurrentOrder()
            //_loadState.emit(LoadState.Success.GoBack)
        }
    }

}
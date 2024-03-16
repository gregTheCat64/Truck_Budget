package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.models.Point
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

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    val orderList = editedRoute.value.orderList.toMutableList()



    fun saveOrder(order: Order){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                //TODO добавить добавление груза в отдельное окно
                //Поправить добавление лишнего заказа, когда мы его редактируем
                orderRepository.insertOrder(order)
                //updateEditedRoute(order)
                orderList.add(order)
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

    fun editPrice(price: Int){
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrder(editedOrder.value.copy(price = price))
        }
    }

    fun editDaysToPay(days: Int){
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.updateOrder(editedOrder.value.copy(daysToPay = days))
        }
    }

    fun editStatus(status: OrderStatus){
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.updateOrder(editedOrder.value.copy(status = status))
        }
    }

    fun editOrder(
        price: Int? = null,
        cargoWeight: Int? = null,
        cargoVolume: Int? = null,
        cargoName: String? = null,
        daysToPay: Int? = null,
        paymentDeadline: LocalDate? = null,
        sentDocsNumber: String? = null,
        docsReceived: LocalDate? = null,
        status: OrderStatus? = null
        ){

        viewModelScope.launch(Dispatchers.IO){
            orderRepository.updateOrder(editedOrder.value.copy(
                price = price?:editedOrder.value.price,
                cargoWeight = cargoWeight?:editedOrder.value.cargoWeight,
                cargoVolume = cargoVolume?:editedOrder.value.cargoVolume,
                cargoName = cargoName?:editedOrder.value.cargoName,
                daysToPay = daysToPay?:editedOrder.value.daysToPay,
                paymentDeadline = paymentDeadline?:editedOrder.value.paymentDeadline,
                sentDocsNumber = sentDocsNumber?:editedOrder.value.sentDocsNumber,
                docsReceived = docsReceived?:editedOrder.value.docsReceived,
                status = status?:editedOrder.value.status
            ))
        }
    }

}
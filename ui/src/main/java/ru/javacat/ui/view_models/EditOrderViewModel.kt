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
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Contractor
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Point
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.domain.repo.RouteRepository
import ru.javacat.ui.LoadState
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditOrderViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val orderRepository: OrderRepository
):ViewModel() {
    val editedOrder = orderRepository.editedItem
    val editedRoute = routeRepository.editedItem

    val isOrderEdited = orderRepository.isEdited

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    init {
        //createEmptyOrder()
    }

    fun getEditedRoute(): Route? {
        return routeRepository.editedItem.value
    }

    fun createEmptyOrder(){
        viewModelScope.launch (Dispatchers.IO){
            orderRepository.createEmptyOrder()
        }
    }

    fun updateEditedOrder(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                //saveRoute()
                val editedOrder = orderRepository.getById(id)
                if (editedOrder != null) {
                    orderRepository.updateEditedItem(editedOrder)
                }
                _loadState.emit(LoadState.Success.OK)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    suspend fun updateEditedRoute(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = routeRepository.getById(id)
            if (result != null) {
                routeRepository.updateEditedItem(result)
            }
        }
    }

    fun saveOrder(order: Order){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                orderRepository.insert(order)
                //val orders = routeRepository.getById(editedRoute.value.id?:0)?.orderList
                val routeToUpdate = editedRoute.value?.copy(
                    startDate = order.date
                )
                Log.i("editOrderVM", "routeToUpdate: $routeToUpdate")

                if (routeToUpdate != null && routeToUpdate.orderList.isEmpty()) {
                    routeRepository.updateRouteToDb(routeToUpdate)
                }

                orderRepository.clearCurrentOrder()

                _loadState.emit(LoadState.Success.GoBack)
            } catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun setOrderFlag(isEdited: Boolean){
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.setOrderFlag(isEdited)
        }
    }

    fun editOrder(
        customer: Company? = null,
        price: Int? = null,
        contractor: Contractor? = null,
        contractorsPrice: Int? = null,
        cargo: Cargo? = null,
        daysToPay: Int? = null,
        paymentDeadline: LocalDate? = null,
        sentDocsNumber: String? = null,
        docsReceived: LocalDate? = null,
        isPaid: Boolean? = null
        ){
        Log.i("orderVM", "editOrder")
        viewModelScope.launch(Dispatchers.IO){
            editedOrder.value?.copy(
                customer = customer?: editedOrder.value?.customer,
                price = price?:editedOrder.value?.price,
                contractor = contractor,
                contractorPrice = contractorsPrice,
                cargo = cargo?: editedOrder.value!!.cargo,
                daysToPay = daysToPay?: editedOrder.value!!.daysToPay,
                paymentDeadline = paymentDeadline?: editedOrder.value!!.paymentDeadline,
                sentDocsNumber = sentDocsNumber?: editedOrder.value!!.sentDocsNumber,
                docsReceived = docsReceived?: editedOrder.value!!.docsReceived,
                isPaidByCustomer = isPaid?:false
            )?.let { orderRepository.updateEditedItem(it) }
        }
    }

    fun removePoint(point: Point){
        val pointList = editedOrder.value?.points?.toMutableList()
        pointList?.remove(point)
        viewModelScope.launch(Dispatchers.IO) {
            if (pointList != null) {
                editedOrder.value?.copy(points = pointList).let {
                    if (it != null) {
                        orderRepository.updateEditedItem(it)
                    }
                }
            }
        }
    }

    fun clearOrder(){
        viewModelScope.launch(Dispatchers.IO){
            orderRepository.clearCurrentOrder()
            //_loadState.emit(LoadState.Success.GoBack)
        }
    }

}
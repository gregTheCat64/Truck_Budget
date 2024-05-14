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
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(
    private val orderRepository: OrderRepository
): ViewModel() {
    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()
    val orders = orderRepository.items

    //filters:
    private var paid: Boolean? = null
    private var year: Year? = null
    private var month: Month? = null
    private var customerId: Long? = null

    fun setPaidFilter(paidParam: Boolean?){
        paid = paidParam
    }

    fun setYearFilter(yearParam: Year?){
        year = yearParam
    }

    fun setMonthFilter(monthParam: Month?){
        month = monthParam
    }

    fun setCustomerFilter(customerIdParam: Long?){
        customerId = customerIdParam
    }

    fun filterOrders(){
        viewModelScope.launch(Dispatchers.IO) {
            //orderRepository.getUnpaidOrders()
            orderRepository.filterOrders(year, month, customerId, paid)
        }
    }

    fun getAllOrders(){
        viewModelScope.launch(Dispatchers.IO) {
            orderRepository.getAll()
        }
    }

//    fun getUnpaidOrders(){
//        viewModelScope.launch(Dispatchers.IO) {
//            //orderRepository.getUnpaidOrders()
//            orderRepository.filterOrders(paid = false)
//        }
//    }

//    fun getOrdersByMonth(month: Month){
//        viewModelScope.launch(Dispatchers.IO) {
//            orderRepository.getOrdersByMonth(month)
//        }
//    }



//    suspend fun getOrderAndUpdateEditedOrder(id: Long) {
//        _loadState.emit(LoadState.Loading)
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                //saveRoute()
//                val editedOrder = orderRepository.getById(id)
//                if (editedOrder != null) {
//                    orderRepository.restoringOrder(editedOrder)
//                }
//                _loadState.emit(LoadState.Success.GoForward)
//            } catch (e: Exception) {
//                _loadState.emit(LoadState.Error(e.message.toString()))
//            }
//        }
//    }
}
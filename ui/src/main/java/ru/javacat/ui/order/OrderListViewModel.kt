package ru.javacat.ui.order

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.FilterOrderModel
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
    val orders = orderRepository.items

    private val _filters = MutableStateFlow(FilterOrderModel())
    val filters: StateFlow<FilterOrderModel> get() = _filters

    //filters:
//    private var paid: Boolean? = null
//    private var year: Int? = LocalDate.now().year
//    private var month: Month? = null
//    private var customerId: Long? = null

    fun setPaidFilter(paidParam: Boolean){
        Log.i("OrderListVM", "setUnpaid $paidParam")
        _filters.value = _filters.value.copy(
            isUnPaid = paidParam
        )
    }

    fun setYearFilter(yearParam: Int?){
        _filters.value = _filters.value.copy(
            year = yearParam
        )
    }

    fun setMonthFilter(monthParam: Month?){
        _filters.value = _filters.value.copy(
            month = monthParam
        )
    }

    fun setCustomerFilter(customerIdParam: Long?, customerNameParam: String?){
        _filters.value = _filters.value.copy(
            customerId = customerIdParam,
            customerName = customerNameParam
        )
    }

    fun filterOrders(){
        Log.i("OrderListVM", "filtering orders")
        viewModelScope.launch(Dispatchers.IO) {
            val year = filters.value.year
            val month = filters.value.month
            val customerId = filters.value.customerId
            val unPaid = filters.value.isUnPaid
            orderRepository.filterOrders(
                year, month, customerId, unPaid)
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
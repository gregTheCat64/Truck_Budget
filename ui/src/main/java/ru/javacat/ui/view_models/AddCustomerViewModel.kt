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
import ru.javacat.domain.models.Customer
import ru.javacat.domain.repo.CustomerRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository
): ViewModel() {

    val editedOrder = orderRepository.editedOrder

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _customers = MutableStateFlow<List<Customer>?>(null)
    val customers = _customers.asStateFlow()

    fun getCustomers(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = customerRepository.getAll()
            _customers.emit(result)
        }
    }

    fun searchCustomers(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = customerRepository.search(search)
            _customers.emit(result)
        }
    }

    fun addCustomerToOrder(t: Customer){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                orderRepository.updateOrder(editedOrder.value.copy(customer = t))
                _loadState.emit(LoadState.Success.GoForward)
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }


}
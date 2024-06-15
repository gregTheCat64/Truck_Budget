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
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Manager
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.ManagersRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val customerRepository: CompaniesRepository,
    private val employeesRepository: ManagersRepository
): ViewModel() {

    val editedOrder = orderRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _customers = MutableStateFlow<List<Company>?>(null)
    val customers = _customers.asStateFlow()

    private val _managers = MutableStateFlow<List<Manager>?>(null)
    val managers = _managers.asStateFlow()

    private var _customer: Company? = null

    private var _manager: Manager? = null


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

    fun setCustomer(customer: Company){
        _customer = customer
    }

    fun setEmployee(manager: Manager){
        _manager = manager
    }

    fun addCustomerToOrder(routeId: Long){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                if (_customer != null) {
                    editedOrder.value.copy(
                        routeId = routeId, customer = _customer!!, manager = _manager)
                        ?.let { orderRepository.updateEditedItem(it) }
                    _loadState.emit(LoadState.Success.OK)
                }
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun getEmployee(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result = employeesRepository.getManagersByCustomerId(id)
            _managers.emit(result)
        }
    }

    fun searchEmployee(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = employeesRepository.search(search)
            _managers.emit(result)
        }
    }


}
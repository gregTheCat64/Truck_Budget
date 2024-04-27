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
import ru.javacat.domain.models.Employee
import ru.javacat.domain.repo.CustomerRepository
import ru.javacat.domain.repo.EmployeesRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val employeesRepository: EmployeesRepository
): ViewModel() {

    val editedOrder = orderRepository.editedOrder

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _customers = MutableStateFlow<List<Customer>?>(null)
    val customers = _customers.asStateFlow()

    private val _employees = MutableStateFlow<List<Employee>?>(null)
    val employees = _employees.asStateFlow()

    private var _customer: Customer? = null

    private var _employee: Employee? = null


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

    fun setCustomer(customer: Customer){
        _customer = customer
    }

    fun setEmployee(employee: Employee){
        _employee = employee
    }

    fun addCustomerToOrder(){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                if (_customer != null && _employee != null) {
                    editedOrder.value?.copy(customer = _customer!!, employee = _employee!!)
                        ?.let { orderRepository.updateOrder(it) }
                    _loadState.emit(LoadState.Success.OK)
                }
            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun getEmployee(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val result = employeesRepository.getEmployeesByCustomerId(id)
            _employees.emit(result)
        }
    }

    fun searchEmployee(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = employeesRepository.search(search)
            _employees.emit(result)
        }
    }


}
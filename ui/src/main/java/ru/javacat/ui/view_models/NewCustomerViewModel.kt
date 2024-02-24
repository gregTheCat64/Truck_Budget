package ru.javacat.ui.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee
import ru.javacat.domain.repo.CustomerRepository
import javax.inject.Inject

@HiltViewModel
class NewCustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
): ViewModel() {

    private val _employees = MutableStateFlow<List<Employee>?>(null)
    val employees = _employees.asStateFlow()

    fun saveNewCustomer(customer: Customer){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(customer)
        }
    }

    fun getEmployeeListByCustomerId(customerId: String){
        Log.i("CustomerVM","getEmployee")
        viewModelScope.launch(Dispatchers.IO) {
            //val result = repository.ge(customerId)
            //_employees.emit(result)
            //Log.i("CusomerVM", "$result")
        }
    }
}
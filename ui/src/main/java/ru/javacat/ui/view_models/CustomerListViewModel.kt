package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.domain.repo.CustomerRepository
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(
    private val customerRepository: CustomerRepository
): ViewModel() {
    var customers = customerRepository.customers
    fun getAllCustomers(){
        viewModelScope.launch(Dispatchers.IO) {
           customerRepository.getAll()
        }
    }
}
package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.repo.CustomerRepository
import javax.inject.Inject

@HiltViewModel
class NewCustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
): ViewModel() {

    fun saveNewCustomer(customer: Customer){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCustomer(customer)
        }

    }
}
package ru.javacat.ui.companies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject

@HiltViewModel
class CustomersDialogViewModel @Inject constructor(
    private val customerRepository: CompaniesRepository
): ViewModel() {
    private val _customers = MutableStateFlow<List<Company>?>(null)
    val customers = _customers.asStateFlow()
    fun getAllCustomers(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = customerRepository.getAll()
            _customers.emit(result)
        }
    }

    fun searchCustomers(s: String){
        viewModelScope.launch {
            val result = customerRepository.search(s)
            _customers.emit(result)
        }
    }
}
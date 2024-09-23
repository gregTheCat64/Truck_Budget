package ru.javacat.ui.companies

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject

@HiltViewModel
class CompanyListViewModel @Inject constructor(
    private val customerRepository: CompaniesRepository
): ViewModel() {
    private var _customers = MutableStateFlow(emptyList<Company>())
    val customers: Flow<List<Company>>
        get() = _customers

    fun getAllCustomers(){
        viewModelScope.launch(Dispatchers.IO) {
           _customers.emit(customerRepository.getAll())
        }
    }

    fun searchCustomers(s: String){
        viewModelScope.launch(Dispatchers.IO) {
            _customers.emit(customerRepository.search(s))
        }
    }
}
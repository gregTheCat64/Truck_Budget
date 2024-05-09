package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.repo.CustomerRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class NewCustomerViewModel @Inject constructor(
    private val repository: CustomerRepository
): ViewModel() {

    var editedCustomer = MutableStateFlow<Customer?>(null)

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()
    suspend fun getCustomerById(id: Long){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                editedCustomer.emit(repository.getById(id))
                _loadState.emit(LoadState.Success.OK)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }

    fun saveNewCustomer(customer: Customer){
        viewModelScope.launch(Dispatchers.IO) {
            _loadState.emit(LoadState.Loading)
            try {
                repository.insert(customer)
                _loadState.emit(LoadState.Success.GoBack)
            }catch (e: Exception) {
                _loadState.emit(LoadState.Error(e.message.toString()))
            }

        }
    }

}
package ru.javacat.ui.edit_order

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
import ru.javacat.domain.repo.CompaniesRepository
import ru.javacat.domain.repo.OrderRepository
import ru.javacat.ui.LoadState
import javax.inject.Inject

@HiltViewModel
class EditCustomerDialogViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val companiesRepository: CompaniesRepository

): ViewModel() {
    val editedOrder = orderRepository.editedItem

    private val _loadState = MutableSharedFlow<LoadState>()
    val loadState = _loadState.asSharedFlow()

    private val _customers = MutableStateFlow<List<Company>?>(null)
    val customers = _customers.asStateFlow()

    fun getCustomers(){
        viewModelScope.launch(Dispatchers.IO) {
            val result = companiesRepository.getAll()
            _customers.emit(result)
        }
    }

    fun searchCustomers(search: String) {
        viewModelScope.launch(Dispatchers.IO){
            val result = companiesRepository.search(search)
            _customers.emit(result)
        }
    }

    fun addCustomerToOrder(customer: Company){
        viewModelScope.launch(Dispatchers.IO){
            _loadState.emit(LoadState.Loading)
            try {
                    editedOrder.value?.copy(
                        customer = customer,
                        manager = null
                        )
                        .let {
                            if (it != null) {
                                orderRepository.updateEditedItem(it)
                            }
                        }
                    _loadState.emit(LoadState.Success.OK)

            }catch (e: Exception){
                _loadState.emit(LoadState.Error(e.message.toString()))
            }
        }
    }
}
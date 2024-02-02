package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val repository: OrderRepository
):ViewModel() {
    private val draftOrder = repository.editedOrder


//    suspend fun updateCustomerInfo(customer: Customer){
//        repository.updateOrder(
//            draftOrder.value.copy(customer = customer)
//        )
//    }
//
//
//    suspend fun editAndSaveOrder(
//        id:String, customer: Customer
//    ){
//        var editedOrder = repository.getOrderById(id)
//        editedOrder = editedOrder.copy(customer = customer)
//        repository.insertOrder(editedOrder)
//    }
}
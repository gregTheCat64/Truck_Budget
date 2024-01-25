package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val repository: OrderRepository
):ViewModel() {
    private val draftOrder = repository.editedOrder
    suspend fun updateCustomerInfo(customerId: Int){
        repository.updateOrder(
            draftOrder.value.copy(customerId = customerId)
        )
    }


    suspend fun editAndSaveOrder(
        id:String, customerId: Int
    ){
        var editedOrder = repository.getOrderById(id)
        editedOrder = editedOrder.copy(customerId = customerId)
        editedOrder.route?.let { repository.insertOrder(it, editedOrder) }
    }
}
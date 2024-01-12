package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    suspend fun updateCargoInfo(
        weight: Int,
        volume: Int,
        cargoName: String,
        extraConditions: String?
    ){
        repository.updateOrder(
            draftOrder.value.copy(
                cargoWeight = weight,
                cargoVolume = volume,
                cargoName = cargoName,
                extraConditions = extraConditions
            )
        )
    }
}
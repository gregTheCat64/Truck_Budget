package ru.javacat.ui.view_models

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.repo.OrderRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddPaymentDetailsViewModel @Inject constructor(
    private val repository: OrderRepository
) {
    private val draftOrder = repository.editedOrder

    suspend fun updatePaymentInfo(price: Long, daysToPay: Int){
        repository.updateOrder(draftOrder.value.copy(
            price = price, daysToPay = daysToPay
        ))
    }

    suspend fun updateDocsDetails(sentDocsNumber: String, docsReceived: LocalDate?){
        repository.updateOrder(draftOrder.value.copy(
            sentDocsNumber = sentDocsNumber, docsReceived = docsReceived
        ))
    }
}
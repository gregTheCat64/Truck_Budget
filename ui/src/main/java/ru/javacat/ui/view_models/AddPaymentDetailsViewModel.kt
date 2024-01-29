package ru.javacat.ui.view_models

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.javacat.domain.repo.OrderRepository
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AddPaymentDetailsViewModel @Inject constructor(
    private val repository: OrderRepository
):ViewModel() {
    private val draftOrder = repository.editedOrder

    suspend fun updatePaymentInfo(price: Int, daysToPay: Int){
        repository.updateOrder(draftOrder.value.copy(
            price = price, daysToPay = daysToPay
        ))
        repository.insertOrder(draftOrder.value) }


    suspend fun updateDocsDetails(sentDocsNumber: String, docsReceived: LocalDate?){
        repository.updateOrder(draftOrder.value.copy(
            sentDocsNumber = sentDocsNumber, docsReceived = docsReceived
        ))
        repository.insertOrder(draftOrder.value)
    }

    suspend fun editAndSaveOrder(orderId: String, price: Int, daysToPay: Int,sentDocsNumber: String, docsReceived: LocalDate?){
        var editedOrder = repository.getOrderById(orderId)
        editedOrder = editedOrder.copy(
            price = price, daysToPay = daysToPay, sentDocsNumber = sentDocsNumber
        )
        //repository.insertOrder(editedOrder)
    }

}
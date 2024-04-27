package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

import ru.javacat.domain.models.Order
import java.time.Month

interface OrderRepository {

    val orders: Flow<List<Order>>

    val editedOrder: StateFlow<Order?>

    val isOrderEdited: SharedFlow<Boolean>

    suspend fun getAllOrders()
    suspend fun getUnpaidOrders()

    suspend fun getOrdersByMonth(month: Month)
    suspend fun setOrderFlag(isEdited: Boolean)

    suspend fun updateOrder(newOrder: Order)

    suspend fun restoringOrder(order: Order)

    suspend fun insertOrder(order: Order)

    suspend fun clearCurrentOrder()

    suspend fun getOrderById(orderId: Long): Order



    suspend fun deleteOrder(order: Order)
}
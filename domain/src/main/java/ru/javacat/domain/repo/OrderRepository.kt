package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

import ru.javacat.domain.models.Order

interface OrderRepository {

    val allOrders: Flow<List<Order?>>

    val editedOrder: StateFlow<Order>

    val isOrderEdited: SharedFlow<Boolean>

    suspend fun setOrderFlag(isEdited: Boolean)

    suspend fun updateOrder(newOrder: Order)

    suspend fun restoringOrder(order: Order)

    suspend fun insertOrder(order: Order)

    suspend fun clearCurrentOrder()

    suspend fun getOrderById(orderId: String): Order

    suspend fun deleteOrder(order: Order)
}
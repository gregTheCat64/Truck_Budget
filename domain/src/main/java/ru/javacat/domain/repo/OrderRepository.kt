package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderCard
import ru.javacat.domain.models.Route

interface OrderRepository {

    val allOrders: Flow<List<Order?>>

    val editedOrder: StateFlow<Order>

    suspend fun updateOrder(newOrder: Order)

    suspend fun insertOrder(order: Order)

    suspend fun clearCurrentOrder()

    suspend fun getOrderById(orderId: String): Order

    suspend fun deleteOrder(order: Order)
}
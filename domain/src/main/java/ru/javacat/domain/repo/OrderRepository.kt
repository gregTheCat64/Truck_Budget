package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.DraftOrder
import ru.javacat.domain.models.Order

interface OrderRepository {

    val allOrders: Flow<List<Order?>>

    val editedOrder: StateFlow<DraftOrder>

    suspend fun updateOrder(newOrder: DraftOrder)

    suspend fun insertOrder(routeId: String, order: Order)

    suspend fun deleteOrder(order: Order)
}
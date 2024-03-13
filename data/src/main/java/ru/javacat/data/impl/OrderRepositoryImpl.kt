package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

val nullOrder = Order("",0L, emptyList(), 0, null,
    0,0,0,0,0,
    null,null,null, null, null,
    null, OrderStatus.IN_PROGRESS)

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
): OrderRepository {

    override val allOrders: Flow<List<Order?>>
        get() = routesDao.getAllOrders().map {list-> list.map { it.toOrderModel() } }

    private val _editedOrder = MutableStateFlow(nullOrder)
    override val editedOrder: StateFlow<Order>
        get() = _editedOrder.asStateFlow()

    override suspend fun updateOrder(newOrder: Order) {
        _editedOrder.emit(newOrder)
        Log.i("orderRepo", "editedOrder = $editedOrder")
    }

    override suspend fun insertOrder(order: Order) {
        Log.i("orderRepo", "order: $order")
        dbQuery { routesDao.insertOrder(order.toDb(), order.points.map { it.toDb(order) }) }
    }

    override suspend fun getOrderById(orderId: String): Order {
        val order = dbQuery { routesDao.getByOrderId(orderId) }
        return order.toOrderModel()
    }

    override suspend fun deleteOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCurrentOrder() {
        _editedOrder.emit(nullOrder)
    }
}
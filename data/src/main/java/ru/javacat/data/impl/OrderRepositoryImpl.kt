package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.OrdersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Order
import ru.javacat.domain.repo.OrderRepository
import java.time.Month
import javax.inject.Inject
import javax.inject.Singleton

val nullOrder: Order? = null

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val ordersDao: OrdersDao
): OrderRepository {

    private val _isOrderEdited = MutableStateFlow(false)
    override val isOrderEdited: StateFlow<Boolean>
        get() = _isOrderEdited.asStateFlow()

    private var _orders = MutableStateFlow(emptyList<Order>())
    override val orders: Flow<List<Order>>
        get() = _orders

    private val _editedOrder = MutableStateFlow(Order())
    override val editedOrder: StateFlow<Order>
        get() = _editedOrder.asStateFlow()

    override suspend fun getAllOrders() {
        //_allOrders = ordersDao.getAllOrders().map {it.toOrderModel() }
        _orders.emit(ordersDao.getAllOrders().map {it.toOrderModel() })
    }

    override suspend fun getUnpaidOrders() {
        _orders.emit(ordersDao.getUnpaidOrder().map { it.toOrderModel()})
    }

    override suspend fun getOrdersByMonth(month: Month) {
        val filteredByMonth = (ordersDao.getAllOrders().map { it.toOrderModel() }).filter { it.points[0].arrivalDate.month == month }
        _orders.emit(filteredByMonth)
    }

    override suspend fun setOrderFlag(isEdited: Boolean) {
        _isOrderEdited.emit(isEdited)
    }
    override suspend fun updateOrder(newOrder: Order) {
        _editedOrder.emit(newOrder)
        _isOrderEdited.emit(true)
        Log.i("orderRepo", "updating order = ${editedOrder.value}")
    }
    override suspend fun restoringOrder(order: Order) {
        _editedOrder.emit(order)
    }

    override suspend fun insertOrder(order: Order) {
        Log.i("orderRepo", "inserting order: $order")
        dbQuery { ordersDao.insertOrder(order.toDb())
        }
        _isOrderEdited.emit(false)
    }

    override suspend fun getOrderById(orderId: Long): Order {
        val order = dbQuery { ordersDao.getByOrderId(orderId) }
        return order.toOrderModel()
    }


    override suspend fun deleteOrder(order: Order) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCurrentOrder() {
        _editedOrder.emit(Order())
        _isOrderEdited.emit(false)
    }
}
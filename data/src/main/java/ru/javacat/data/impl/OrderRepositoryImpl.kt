package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderStatus
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

val nullOrder = Order("",0L, emptyList(), 0, Customer(null, ""),
    Cargo(0,0,"",true,false,false),"",0,
    null,null,null,
    false)

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
): OrderRepository {

    private val _isOrderEdited = MutableStateFlow(false)
    override val isOrderEdited: StateFlow<Boolean>
        get() = _isOrderEdited.asStateFlow()
    override val allOrders: Flow<List<Order?>>
        get() = routesDao.getAllOrders().map {list-> list.map { it.toOrderModel() } }

    private val _editedOrder = MutableStateFlow(nullOrder)
    override val editedOrder: StateFlow<Order>
        get() = _editedOrder.asStateFlow()

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
        dbQuery { routesDao.insertOrder(order.toDb())
        }
        _isOrderEdited.emit(false)
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
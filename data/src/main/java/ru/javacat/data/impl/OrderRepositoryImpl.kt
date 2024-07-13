package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.OrdersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Order
import ru.javacat.domain.repo.OrderRepository
import java.time.LocalDate
import java.time.Month
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

var emptyOrder: Order = Order(
    date = LocalDate.now()
)

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val ordersDao: OrdersDao
): OrderRepository {

    override val lastOrder: Order?
        get() = ordersDao.getLastOrder()?.toOrderModel()

    private val _isOrderEdited = MutableStateFlow(false)
    override val isEdited: StateFlow<Boolean>
        get() = _isOrderEdited.asStateFlow()

    private var _orders = MutableStateFlow(emptyList<Order>())
    override val items: Flow<List<Order>>
        get() = _orders

    private val _editedOrder = MutableStateFlow<Order?>(null)
    override val editedItem: StateFlow<Order?>
        get() = _editedOrder.asStateFlow()

    override suspend fun getAll() {
        _orders.emit(ordersDao.getAllOrders().map {it.toOrderModel() })
    }

    override suspend fun getUnpaidOrders() {
        //_orders.emit(ordersDao.getUnpaidOrder().map { it.toOrderModel()})
        val filtered = items.map { it.filter { it.isPaidByCustomer == false } }
    }


    override suspend fun getOrdersByMonth(month: Month) {
        val filteredByMonth = (ordersDao.getAllOrders().map { it.toOrderModel() }).filter { it.points[0].arrivalDate.month == month }
        _orders.emit(filteredByMonth)
    }

    override suspend fun filterOrders(year: Year?, month: Month?, customerId: Long?, paid: Boolean?){
        Log.i("orderRepo", "filter: year: $year, month: $month, customerId: $customerId, paid: $paid")
        var orders = ordersDao.getAllOrders().map { it.toOrderModel()}

        if (year != null) {
            orders = orders.filter { it.date.year.equals(year)}
        }

        if (month != null) {
            orders = orders.filter { it.date.month == month }
        }

        if (customerId != null) {
            orders = orders.filter { it.customer?.id == customerId }
        }

        if (paid != null) {
            orders = orders.filter { it.isPaidByCustomer == paid }
        }

        _orders.emit(orders)

    }

    override suspend fun setOrderFlag(isEdited: Boolean) {
        _isOrderEdited.emit(isEdited)
    }
    override suspend fun updateEditedItem(t: Order) {
        _editedOrder.emit(t)
        _isOrderEdited.emit(true)
        Log.i("orderRepo", "updating order = ${editedItem.value}")
    }


    override suspend fun insert(order: Order): Long {
        Log.i("orderRepo", "inserting order: $order")
        val result = dbQuery { ordersDao.insertOrder(order.toDb())
        }
        _isOrderEdited.emit(false)
        return result
    }

    override suspend fun updateOrderToDb(order: Order) {
        dbQuery { ordersDao.updateOrder(order.toDb()) }
    }

    override suspend fun getById(orderId: Long): Order {
        val order = dbQuery { ordersDao.getByOrderId(orderId) }
        return order.toOrderModel()
    }


    override suspend fun removeById(orderId: Long) {
        dbQuery { ordersDao.removeOrder(orderId) }
    }

    override suspend fun createEmptyOrder() {
        emptyOrder = emptyOrder.copy(cargo = Cargo(
            lastOrder?.cargo?.cargoWeight?:10,
            lastOrder?.cargo?.cargoVolume?:32
        ))
        _editedOrder.emit(emptyOrder)
    }

    override suspend fun clearCurrentOrder() {
        _editedOrder.emit(null)
        //_isOrderEdited.emit(false)
    }
}
package ru.javacat.data.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.OrdersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.Order
import ru.javacat.domain.repo.OrderRepository
import java.time.LocalDate
import java.time.Month
import java.time.Year
import javax.inject.Inject
import javax.inject.Singleton

var emptyOrder: Order = Order(
    date = LocalDate.now(),
    cargo = Cargo()
)

@Singleton
class OrderRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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

    override suspend fun getLastOrders(numberOfOrders: Int): List<Order> {
        return ordersDao.getLastOrders(numberOfOrders).map { it.toOrderModel() }
    }

    override suspend fun getAllByYear(year: Int) {
        _orders.emit(ordersDao.getAllOrdersByYear(year.toString()).map {it.toOrderModel() })
    }

    override suspend fun getCompanyOrdersCountByYear(year: String): Int {
        return dbQuery { ordersDao.getCountCompanyOrdersByYear(year) }
    }

    override suspend fun getNotCompanyOrdersCountByYear(year: String): Int {
        return dbQuery { ordersDao.getCountNotCompanyOrdersByYear(year) }
    }

    override suspend fun getUnpaidOrders() {
        //_orders.emit(ordersDao.getUnpaidOrder().map { it.toOrderModel()})
        val filtered = items.map { it.filter { it.isPaidByCustomer == false } }
    }


    override suspend fun getOrdersByMonth(month: Month) {
        val filteredByMonth = (ordersDao.getAllOrders().map { it.toOrderModel() }).filter { it.points[0].arrivalDate.month == month }
        _orders.emit(filteredByMonth)
    }

    override suspend fun filterOrders(year: Int?, month: Month?, customerId: Long?, unPaid: Boolean){
        Log.i("orderRepo", "filter: year: $year, month: $month, customerId: $customerId, unPaid: $unPaid")
        var orders = ordersDao.getAllOrdersByYear(year.toString()).map { it.toOrderModel()}

//        if (year != null) {
//            orders = orders.filter { it.date.year == year }
//        }

        if (month != null) {
            orders = orders.filter { it.date.month == month }
        }

        if (customerId != null) {
            orders = orders.filter { it.customer?.id == customerId }
        }

        if (unPaid) {
            orders = orders.filter { !it.isPaidByCustomer }
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


    override suspend fun insert(t: Order): Long {
        Log.i("orderRepo", "inserting order: $t")
        val result = dbQuery {
            switchDatabaseModified(context, true)
            ordersDao.insertOrder(t.toDb())
        }
        _isOrderEdited.emit(false)
        return result
    }

    override suspend fun updateOrderToDb(order: Order) {
        dbQuery {
            switchDatabaseModified(context, true)
            ordersDao.updateOrder(order.toDb()) }
    }

    override suspend fun getById(orderId: Long): Order {
        val order = dbQuery { ordersDao.getByOrderId(orderId) }
        return order.toOrderModel()
    }


    override suspend fun removeById(orderId: Long) {
        dbQuery {
            switchDatabaseModified(context, true)
            ordersDao.removeOrder(orderId) }
    }

    override suspend fun createEmptyOrder() {
        Log.i("OrderRepo", "lastOrder: $lastOrder")

//        emptyOrder = emptyOrder.copy(cargo = Cargo(
//            lastOrder?.cargo?.cargoWeight?:10,
//            lastOrder?.cargo?.cargoVolume?:32
//        ))
        Log.i("OrderRepo", "emptyOrder: $emptyOrder")
        _editedOrder.emit(emptyOrder)
    }

    override suspend fun clearCurrentOrder() {
        _editedOrder.emit(null)
        //_isOrderEdited.emit(false)
    }
}
package ru.javacat.data.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import ru.javacat.data.db.dao.RoutesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.DraftOrder
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject
import javax.inject.Singleton

val draftOrder = DraftOrder(null,null,null, null, null,
    null,null,null,null,null,
    null,null,null, null)

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val routesDao: RoutesDao
): OrderRepository {

    override val allOrders: Flow<List<Order?>>
        get() = routesDao.getAllOrders().map {list-> list.map { it.toOrderModel() } }

    private val _editedOrder = MutableStateFlow(draftOrder)
    override val editedOrder: StateFlow<DraftOrder>
        get() = _editedOrder.asStateFlow()

    override suspend fun updateOrder(newOrder: DraftOrder) {
        _editedOrder.emit(newOrder)
    }

    override suspend fun insertOrder(routeID:String, order: Order) {
        dbQuery { routesDao.insertOrder(order.toDb(routeID), order.points.map { it.toDb(order) }) }
    }

    override suspend fun deleteOrder(order: Order) {
        TODO("Not yet implemented")
    }
}
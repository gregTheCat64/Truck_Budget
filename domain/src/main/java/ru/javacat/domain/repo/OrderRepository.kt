package ru.javacat.domain.repo

import ru.javacat.domain.models.Order
import java.time.Month
import java.time.Year

interface OrderRepository: BaseRepository<Order, Long> {

    //val orders: Flow<List<Order>>

    //val editedOrder: StateFlow<Order?>

    //val isOrderEdited: SharedFlow<Boolean>

//    suspend fun getAllOrders()
    suspend fun getUnpaidOrders()

    suspend fun filterOrders(year: Year? = null, month: Month? = null, customerId: Long? = null, paid: Boolean? = null)

    suspend fun getOrdersByMonth(month: Month)
    suspend fun setOrderFlag(isEdited: Boolean)

    //suspend fun updateOrder(newOrder: Order)

    suspend fun restoringOrder(order: Order)

    //suspend fun insertOrder(order: Order)

    suspend fun clearCurrentOrder()

    //suspend fun getOrderById(orderId: Long): Order

    //suspend fun deleteOrder(order: Order)
}
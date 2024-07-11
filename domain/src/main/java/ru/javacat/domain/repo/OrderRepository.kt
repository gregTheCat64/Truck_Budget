package ru.javacat.domain.repo

import ru.javacat.domain.models.Order
import java.time.Month
import java.time.Year

interface OrderRepository: BaseRepository<Order, Long> {

    suspend fun getUnpaidOrders()
    suspend fun filterOrders(year: Year? = null, month: Month? = null, customerId: Long? = null, paid: Boolean? = null)

    suspend fun updateOrderToDb(order: Order)

    suspend fun getOrdersByMonth(month: Month)
    suspend fun setOrderFlag(isEdited: Boolean)
    suspend fun clearCurrentOrder()

    suspend fun createEmptyOrder()


}
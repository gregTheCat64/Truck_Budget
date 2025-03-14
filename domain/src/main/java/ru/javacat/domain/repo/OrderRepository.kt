package ru.javacat.domain.repo

import ru.javacat.domain.models.Order
import java.time.Month
import java.time.Year

interface OrderRepository: BaseRepository<Order, Long> {

    val lastOrder: Order?
    suspend fun getUnpaidOrders()

    suspend fun getLastOrders(numberOfOrders: Int): List<Order>

    suspend fun getCompanyOrdersCountByYear(year: String): Int
    suspend fun getNotCompanyOrdersCountByYear(year: String): Int

    suspend fun filterOrders(year: Int? = null, month: Month? = null, customerId: Long? = null, unPaid: Boolean)

    suspend fun updateOrderToDb(order: Order)

    suspend fun getOrdersByMonth(month: Month)
    suspend fun setOrderFlag(isEdited: Boolean)
    suspend fun clearCurrentOrder()

    suspend fun createEmptyOrder()


}
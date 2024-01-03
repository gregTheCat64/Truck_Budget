package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.domain.models.Order

data class OrderWithPointsAndCustomer (
    @Embedded
    val order: DbOrder,

    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val points: List<DbPoint>,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "atiNumber"
    )
    val customer: CustomerWithEmployees
) {
    fun toOrderModel(): Order {
        return Order(
            id = order.id,
            routeId = order.routeId,
            points = points.map { it.toPointModel() },
            price = order.price,
            customer = customer.toCustomerModel(),
            paymentDeadline = order.paymentDeadline?.toLocalDate(),
            sentDocsNumber = order.sentDocsNumber,
            docsReceived = order.docsReceived?.toLocalDate(),
            status = order.status
        )
    }
}
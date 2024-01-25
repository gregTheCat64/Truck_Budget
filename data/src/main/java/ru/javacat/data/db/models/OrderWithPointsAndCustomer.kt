package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route

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
        entityColumn = "atiNumber",
        entity = Customer::class
    )
    val customer: Customer,

    @Relation(
        parentColumn = "routeId",
        entityColumn = "id",
        entity = Route::class
    )
    val route: Route


) {
    fun toOrderModel(): Order {
        return Order(
            id = order.id,
            routeId = route.id,
            points = points.map { it.toPointModel() },
            price = order.price,
            customer = customer,
            cargoWeight = order.cargoWeight,
            cargoVolume  = order.cargoVolume,
            cargoName = order.cargoName,
            extraConditions = order.extraConditions,
            daysToPay = order.daysToPay,
            paymentDeadline = order.paymentDeadline?.toLocalDate(),
            sentDocsNumber = order.sentDocsNumber,
            docsReceived = order.docsReceived?.toLocalDate(),
            status = order.status
        )
    }
}
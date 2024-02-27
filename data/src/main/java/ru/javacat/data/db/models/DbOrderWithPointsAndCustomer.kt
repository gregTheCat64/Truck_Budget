package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbPoint
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbStaff
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.domain.models.Order

data class DbOrderWithPointsAndCustomer (
    @Embedded
    val order: DbOrder,

    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val points: List<DbPoint>,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "id",
        entity = DbCustomer::class
    )
    val customer: DbCustomer,

    @Relation(
        parentColumn = "routeId",
        entityColumn = "id",
    )
    val route: DbRoute,

    @Relation(
        parentColumn = "driverId",
        entityColumn = "id",
    )
    val driver: DbStaff,

    @Relation(
        parentColumn = "truckId",
        entityColumn = "id",
    )
    val truck: DbTruck,

    @Relation(
        parentColumn = "trailerId",
        entityColumn = "id",
    )
    val trailer: DbTrailer?,


    ) {
    fun toOrderModel(): Order {
        return Order(
            id = order.id,
            routeId = route.id?:0L,
            points = points.map { it.toPointModel() },
            driverId = driver.id,
            truckId = truck.id,
            trailerId = trailer?.id?:0,
            price = order.price,
            customer = customer.toCustomerModel(),
            cargoWeight = order.cargoWeight?:0,
            cargoVolume  = order.cargoVolume?:0,
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
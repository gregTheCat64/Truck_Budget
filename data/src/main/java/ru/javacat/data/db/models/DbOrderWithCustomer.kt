package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbCompany
import ru.javacat.data.db.entities.DbManager
import ru.javacat.data.db.entities.DbOrder

import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbTruckDriver
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.domain.models.Contractor
import ru.javacat.domain.models.Order

data class DbOrderWithCustomer(
    @Embedded
    val order: DbOrder,

//    @Relation(
//        parentColumn = "id",
//        entityColumn = "orderId"
//    )
//    @Embedded
//    val points: List<Point>,

    @Relation(
        parentColumn = "customerId",
        entityColumn = "id",
        entity = DbCompany::class
    )
    val customer: DbCompanyWithManagers,

    @Relation(
        parentColumn = "managerId",
        entityColumn = "id",
    )
    val manager: DbManager?,

    @Relation(
        parentColumn = "contractorId",
        entityColumn = "id",
        entity = DbCompany::class
    )
    val contractor: DbCompanyWithManagers?,

    @Relation(
        parentColumn = "routeId",
        entityColumn = "id",
    )
    val route: DbRoute,

    @Relation(
        parentColumn = "driverId",
        entityColumn = "id",
    )
    val driver: DbTruckDriver?,

    @Relation(
        parentColumn = "truckId",
        entityColumn = "id",
    )
    val truck: DbTruck?,

    @Relation(
        parentColumn = "trailerId",
        entityColumn = "id",
    )
    val trailer: DbTrailer?,


    ) {
    fun toOrderModel(): Order {
        return Order(
            id = order.id,
            routeId = route.id,
            points = order.points.map { it.toPointModel() },
            date = order.date.toLocalDate(),
            price = order.price,
            payType = order.payType,
            contractorPrice = order.contractorPrice,
            payTypeToContractor = order.payTypeToContractor,
            commission = order.commission,
            customer = customer.toCompanyModel(),
            manager = manager?.toManagerModel(),
            contractor = Contractor(
                contractor?.toCompanyModel(),
                driver?.toTruckDriverModel(),
                truck?.toTruck(),
                trailer?.toTrailer()
            ),
            cargo = order.cargo,
            extraConditions = order.extraConditions,
            daysToPay = order.daysToPay,
            daysToPayToContractor = order.daysToPayToContractor,
            paymentDeadline = order.paymentDeadline?.toLocalDate(),
            sentDocsNumber = order.sentDocsNumber,
            docsReceived = order.docsReceived?.toLocalDate(),
            isPaidByCustomer = order.isPaidByCustomer,
            isPaidToContractor = order.isPaidToContractor
        )
    }
}
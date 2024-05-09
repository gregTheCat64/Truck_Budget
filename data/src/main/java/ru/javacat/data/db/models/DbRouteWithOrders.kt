package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbTruckDriver
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.domain.models.Route

data class DbRouteWithOrders (
    @Embedded
    val route: DbRoute,

    @Relation(
        parentColumn = "id",
        entityColumn = "routeId",
        entity = DbOrder::class
    )
    val orders: List<DbOrderWithCustomer>,

    @Relation(
        parentColumn = "driverId",
        entityColumn = "id",
        entity = DbTruckDriver::class
    )
    val driver: DbTruckDriver,

    @Relation(
        parentColumn = "truckId",
        entityColumn = "id",
        entity = DbTruck::class
    )
    val truck: DbTruck,

    @Relation(
        parentColumn = "trailerId",
        entityColumn = "id",
        entity = DbTrailer::class
    )
    val trailer: DbTrailer?

) {
    fun toRouteModel(): Route {
        return Route(
            id = route.id,
            startDate = route.startDate?.toLocalDate(),
            endDate = route.endDate?.toLocalDate(),
            driver = driver.toTruckDriverModel(),
            truck = truck.toTruck(),
            trailer = trailer?.toTrailer(),
            orderList = orders.map { it.toOrderModel() },
            prepayment = route.prepayment,
            fuelUsedUp = route.fuelUsedUp,
            fuelPrice = route.fuelPrice,
            routeSpending = route.routeSpending,
            payPerDiem = route.payPerDiem,
            routeDuration = route.routeDuration,
            driverSalary = route.driverSalary,
            moneyToPay = route.moneyToPay,
            income = route.income,
            netIncome = route.netIncome,
            isFinished = route.isFinished
        )
    }
}
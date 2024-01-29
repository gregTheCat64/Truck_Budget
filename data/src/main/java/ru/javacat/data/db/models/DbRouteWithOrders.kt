package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbStaff
import ru.javacat.data.db.entities.DbVehicle
import ru.javacat.domain.models.Route

data class DbRouteWithOrders (
    @Embedded
    val route: DbRoute,

    @Relation(
        parentColumn = "id",
        entityColumn = "routeId",
        entity = DbOrder::class
    )
    val orders: List<DbOrderWithPointsAndCustomer>,

    @Relation(
        parentColumn = "driverId",
        entityColumn = "id",
        entity = DbStaff::class
    )
    val driver: DbStaff,

    @Relation(
        parentColumn = "truckId",
        entityColumn = "id",
        entity = DbVehicle::class
    )
    val truck: DbVehicle,

    @Relation(
        parentColumn = "trailerId",
        entityColumn = "id",
        entity = DbVehicle::class
    )
    val trailer: DbVehicle

) {
    fun toRouteModel(): Route {
        return Route(
            id = route.id,
            startDate = route.startDate.toLocalDate(),
            endDate = route.endDate?.toLocalDate(),
            driver = driver.toStaff(),
            truck = truck.toVehicle(),
            trailer = trailer.toVehicle(),
            orderList = orders.map { it.toOrderModel() },
            prepayment = route.prepayment,
            fuelUsedUp = route.fuelUsedUp,
            fuelPrice = route.fuelPrice,
            routeExpenses = route.routeExpenses,
            routeDuration = route.routeDuration,
            driverSalary = route.driverSalary,
            moneyToPay = route.moneyToPay,
            income = route.income,
            netIncome = route.netIncome,
            isFinished = route.isFinished
        )
    }
}
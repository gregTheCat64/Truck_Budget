package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbCustomer
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route

data class RouteWithOrders (
    @Embedded
    val route: DbRoute,

    @Relation(
        parentColumn = "id",
        entityColumn = "routeId",
        entity = DbOrder::class
    )
    val orders: List<OrderWithPointsAndCustomer>,

) {
    fun toRouteModel(): Route {
        return Route(
            id = route.id,
            startDate = route.startDate.toLocalDate(),
            endDate = route.endDate?.toLocalDate(),
            driver = route.driver,
            truck = route.truck,
            trailer = route.trailer,
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
package ru.javacat.data.db.models

import androidx.room.Embedded
import androidx.room.Relation
import ru.javacat.common.utils.toLocalDate
import ru.javacat.data.db.entities.DbOrder
import ru.javacat.data.db.entities.DbCompany
import ru.javacat.data.db.entities.DbRoute
import ru.javacat.data.db.entities.DbTruckDriver
import ru.javacat.data.db.entities.DbTrailer
import ru.javacat.data.db.entities.DbTruck
import ru.javacat.domain.models.Contractor
import ru.javacat.domain.models.Route
import ru.javacat.domain.models.SalaryParameters
import java.time.LocalDate

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
        parentColumn = "contractorId",
        entityColumn = "id",
        entity = DbCompany::class
    )
    val company: DbCompanyWithManagers,

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
    val trailer: DbTrailer?,



) {
    fun toRouteModel(): Route {
        return Route(
            id = route.id,
            startDate = route.startDate?.toLocalDate()?: LocalDate.now(),
            endDate = route.endDate?.toLocalDate(),
            contractor = Contractor(
                company.toCompanyModel(),
                driver.toTruckDriverModel(),
                truck.toTruck(),
                trailer?.toTrailer()
            ),
            orderList = orders.map { it.toOrderModel() },
            routeDetails = route.routeDetails,
            salaryParameters = route.salaryParameters?: SalaryParameters(),
            prepayment = route.prepayment,
            driverSalary = route.driverSalary,
            contractorsCost = route.contractorsCost,
            totalExpenses = route.totalExpenses,
            moneyToPay = route.moneyToPay,
            isPaidToContractor = route.isPaidToContractor,
            revenue = route.revenue,
            profit = route.profit,
            isFinished = route.isFinished,
        )
    }
}
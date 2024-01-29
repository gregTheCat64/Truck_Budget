package ru.javacat.ui.view_models

import ru.javacat.domain.models.Order
import ru.javacat.domain.models.OrderCard
import ru.javacat.domain.repo.RouteRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FinishRouteViewModel @Inject constructor(
    private val repository: RouteRepository
) {
    private val route = repository.editedRoute

    suspend fun updateRoute(
        id: String,
        startDate: LocalDate,
        endDate: LocalDate,
        orderList: List<Order>,
        fuelUsedUp: Int,
        fuelPrice: Int,
        routeExpenses: Int,
        routeDuration: Int,
        driverSalary: Int,
        moneyToPay: Int,
        income: Int,
        netIncome: Int
    ){
        var editedRoute = repository.getRoute(id)
        editedRoute = editedRoute.copy(
            startDate = startDate,
            endDate = endDate,
            orderList = orderList,
            fuelUsedUp = fuelUsedUp,
            fuelPrice = fuelPrice,
            routeExpenses = routeExpenses,
            routeDuration = routeDuration,
            driverSalary = driverSalary,
            moneyToPay = moneyToPay,
            income = income,
            netIncome = netIncome,
            isFinished = true
        )
        repository.insertRoute(editedRoute)
    }
}
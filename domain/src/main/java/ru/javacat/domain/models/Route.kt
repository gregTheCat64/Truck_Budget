package ru.javacat.domain.models

import java.time.LocalDate

data class Route(
    val id: Long = 0L,
    val startDate: LocalDate? = LocalDate.now(),
    val endDate: LocalDate? = null,
    val contractor: Contractor? = null,
    val orderList: List<Order> = emptyList(),

    val routeDetails: RouteDetails? = null,
    val salaryParameters: SalaryParameters? = null,
    val prepayment: Int = 0,
    val driverSalary: Float = 0F,
    val contractorsCost: Int = 0,
    val totalExpenses: Float = 0F,

    val moneyToPay: Float? = 0F,
    val isPaidToContractor: Boolean = false,
    val revenue: Int = 0,
    val profit: Float = 0f,

    val isFinished: Boolean = false
    )

data class RouteDetails(
    val fuelUsedUp: Int = 0,
    val fuelPrice: Float = 0F,
    val extraExpenses: Int = 0,
    val roadFee: Int = 0,
    val extraPoints: Int = 0,
    val routeDuration: Int = 0,
    val routeDistance: Int = 0,
)

data class Contractor(
    val company: Company? = null,
    val driver: TruckDriver? = null,
    val truck: Truck? = null,
    val trailer: Trailer? = null,
)

data class SalaryParameters(
    val salaryCountMethod: SalaryCountMethod? = null,
    val costPerDiem: Int = 0,

    val profitPercentage: Int = 0,
    val revenuePercentage: Int = 0,
    val costPerKilometer: Float = 0f,

    val extraPointsCost: Int = 0,
)

enum class SalaryCountMethod{
    BY_PROFIT,
    BY_REVENUE,
    BY_DISTANCE
}
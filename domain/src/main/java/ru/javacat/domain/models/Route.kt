package ru.javacat.domain.models

import java.time.LocalDate

data class Route(
    val id: Long = 0L,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val contractor: Contractor? = null,
    val orderList: List<Order> = emptyList(),
    val countRoute: CountRoute? = null,

    val contractorsCost: Int = 0,
    val routeExpenses: Int = 0,

    val revenue: Int? = null,
    val profit: Int? = null,

    val isFinished: Boolean = false
    )
data class Contractor(
    val company: Company? = null,
    val driver: TruckDriver? = null,
    val truck: Truck? = null,
    val trailer: Trailer? = null,
)
data class CountRoute(
    val prepayment: Int?,
    val fuelUsedUp: Int? = null,
    val fuelPrice: Float? = null,
    val otherExpenses: Int? = null,
    val payPerDiem: Int? = null,
    val routeDuration: Int? = null,
    val driverSalary: Int? = null,
    val moneyToPay: Int? = null,
)
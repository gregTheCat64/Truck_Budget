package ru.javacat.domain.models

import java.time.LocalDate

data class Route(
    val id: Long? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val driver: Staff? = null,
    val truck: Truck? = null,
    val trailer: Trailer? = null,
    val orderList: List<Order> = emptyList(),
    val prepayment: Int? = null,
    val fuelUsedUp: Int? = null,
    val fuelPrice: Int? = null,
    val routeSpending: Int? = null,
    val routeDuration: Int? = null,
    val driverSalary: Int? = null,
    val moneyToPay: Int? = null,
    val income: Int? = null,
    val netIncome: Int? = null,
    val isFinished: Boolean = false
    )

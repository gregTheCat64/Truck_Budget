package ru.javacat.domain.models

import java.time.LocalDate

data class Route(
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val driver: TruckDriver,
    val truck: Vehicle,
    val trailer: Vehicle?,
    val orderList: List<Order>,
    val prepayment: Long?,
    val fuelUsedUp: Int?,
    val fuelPrice: Long?,
    val routeExpenses: Long?,
    val routeDuration: Int?,
    val driverSalary: Long?,
    val moneyToPay: Long?,
    val income: Long?,
    val netIncome: Long?,
    val isFinished: Boolean
    )
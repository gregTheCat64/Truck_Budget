package ru.javacat.domain.models

import java.time.LocalDate

data class Route(
    val id: String,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val driver: Staff?,
    val truck: Vehicle?,
    val trailer: Vehicle?,
    val orderList: List<Order>,
    val prepayment: Int?,
    val fuelUsedUp: Int?,
    val fuelPrice: Int?,
    val routeExpenses: Int?,
    val routeDuration: Int?,
    val driverSalary: Int?,
    val moneyToPay: Int?,
    val income: Int?,
    val netIncome: Int?,
    val isFinished: Boolean
    )

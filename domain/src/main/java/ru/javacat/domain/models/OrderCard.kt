package ru.javacat.domain.models

import java.time.LocalDate

data class OrderCard(
    val id: String,
    val routeId: String,
    val points: List<Point>,
    val price: Int,
    val customerName: String,
    val driverName: String,
    val truck: String,
    val trailer: String,
    val cargoWeight: Int,
    val cargoVolume: Int,
    val cargoName: String?,
    val extraConditions: String?,
    val daysToPay: Int?,
    val paymentDeadline: LocalDate?,
    val sentDocsNumber: String?,
    val docsReceived: LocalDate?,
    val status: OrderStatus?
)
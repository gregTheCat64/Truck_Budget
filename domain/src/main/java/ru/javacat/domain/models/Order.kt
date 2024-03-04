package ru.javacat.domain.models

import java.time.LocalDate

data class Order(
    val id: String,
    val routeId: Long,
    val points: List<Point>,
    val price: Int,
    val customer: Customer?,
    val driverId: Long,
    val truckId: Long,
    val trailerId: Long?,
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

enum class OrderStatus(){
    IN_PROGRESS, WAITING_FOR_PAYMENT, PAID
}

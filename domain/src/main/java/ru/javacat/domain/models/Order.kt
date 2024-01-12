package ru.javacat.domain.models

import java.time.LocalDate

data class Order(
    val id: Int,
    val route: Route,
    val points: List<Point>,
    val price: Int,
    val customerId: Int,
    val cargoWeight: Int,
    val cargoVolume: Int,
    val cargoName: String,
    val extraConditions: String,
    val daysToPay: Int,
    val paymentDeadline: LocalDate?,
    val sentDocsNumber: String?,
    val docsReceived: LocalDate?,
    val status: OrderStatus
)

enum class OrderStatus(){
    IN_PROGRESS, DOCS_SHIPPED, WAITING_FOR_PAYMENT, PAID
}

data class DraftOrder(
    val id: Int?,
    val route: Route,
    val points: List<Point>?,
    val price: Long?,
    val customerId: Int?,
    val cargoWeight: Int?,
    val cargoVolume: Int?,
    val cargoName: String?,
    val extraConditions: String?,
    val daysToPay: Int?,
    val paymentDeadline: LocalDate?,
    val sentDocsNumber: String?,
    val docsReceived: LocalDate?,
    val status: OrderStatus?
)
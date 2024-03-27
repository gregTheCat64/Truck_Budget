package ru.javacat.domain.models

import java.time.LocalDate

data class Order(
    val id: String,
    val routeId: Long,
    val points: List<Point>,
    val price: Int,
    val customer: Customer,
    val cargo: Cargo,
    val extraConditions: String?,
    val daysToPay: Int?,
    val paymentDeadline: LocalDate?,
    val sentDocsNumber: String?,
    val docsReceived: LocalDate?,
    val isPaid: Boolean = false
)

enum class OrderStatus(){
    IN_PROGRESS, WAITING_FOR_PAYMENT, PAID
}

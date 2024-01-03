package ru.javacat.domain.models

import java.time.LocalDate

data class Order(
    val id: Int,
    val routeId: String,
    val points: List<Point>,
    val price: Int,
    val customer: Customer,
    val paymentDeadline: LocalDate?,
    val sentDocsNumber: String?,
    val docsReceived: LocalDate?,
    val status: OrderStatus
)

enum class OrderStatus(){
    IN_PROGRESS, DOCS_SHIPPED, WAITING_FOR_PAYMENT, PAID
}
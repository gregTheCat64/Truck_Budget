package ru.javacat.domain.models

import java.time.LocalDate

data class Order(
    val id: Long = 0L,
    val routeId: Long = 0L,
    val points: List<Point> = emptyList(),
    val price: Int = 0,
    val customer: Customer? = null,
    val employee: Employee? = null,
    val driver: TruckDriver? = null,
    val truck: Truck? = null,
    val trailer: Trailer? = null,
    val cargo: Cargo? = null,
    val extraConditions: String? = null,
    val daysToPay: Int? = null,
    val paymentDeadline: LocalDate? = null,
    val sentDocsNumber: String? = null,
    val docsReceived: LocalDate? = null,
    val isPaid: Boolean = false
)

enum class OrderStatus(){
    IN_PROGRESS, WAITING_FOR_PAYMENT, PAID
}

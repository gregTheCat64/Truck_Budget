package ru.javacat.domain.models

import java.time.LocalDate

data class Order(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startLocation: String,
    val endLocation: String,
    val price: Int,
    val customer: Customer,
    val paymentDeadline: LocalDate?,
    val sentDocsNumber: String?,
    val docsReceived: LocalDate?,
    val isPaid: Boolean
)
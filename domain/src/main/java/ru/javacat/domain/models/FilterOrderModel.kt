package ru.javacat.domain.models

import java.time.LocalDate
import java.time.Month

data class FilterOrderModel (
    val year: Int? = LocalDate.now().year,
    val month: Month? = null,
    val isUnPaid: Boolean = false,
    val customerId: Long? = null,
    val customerName: String? = null
)
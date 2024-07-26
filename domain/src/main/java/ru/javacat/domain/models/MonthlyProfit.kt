package ru.javacat.domain.models

import java.time.LocalDate
import java.time.Month

data class MonthlyProfit (
    val monthDate: Month?,
    val totalProfit: Long
)
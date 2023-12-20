package ru.javacat.domain.models

import java.time.LocalDate

data class Expenses (
    val name: String,
    val date: LocalDate,
    val cost: Long,
)
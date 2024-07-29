package ru.javacat.domain.models

import java.time.LocalDate

data class Expense (
    val id: Long? = null,
    val name: String,
    val description: String,
    val date: LocalDate,
    val price: Int
)
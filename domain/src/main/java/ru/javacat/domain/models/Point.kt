package ru.javacat.domain.models

import java.time.LocalDate

data class Point(
    val id: Int,
    val location: Location,
    val arrivalDate: LocalDate
)

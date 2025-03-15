package ru.javacat.domain.models

import java.time.LocalDate

data class Point(
    val position: Int,
    val location: String,
    var arrivalDate: LocalDate
)

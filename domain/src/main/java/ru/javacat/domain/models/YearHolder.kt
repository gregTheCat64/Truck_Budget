package ru.javacat.domain.models

import java.time.LocalDateTime

object YearHolder {
    var selectedYear: Int = LocalDateTime.now().year
}
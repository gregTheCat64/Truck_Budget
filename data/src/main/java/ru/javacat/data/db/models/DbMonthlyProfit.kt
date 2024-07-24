package ru.javacat.data.db.models

import ru.javacat.common.utils.toLocalDate
import ru.javacat.domain.models.MonthlyProfit
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset

data class DbMonthlyProfit (
    val monthDate: Long,
    val totalProfit: Long
) {
    fun toMonthlyProfitModel(): MonthlyProfit{
        return MonthlyProfit(
            Instant.ofEpochSecond(monthDate).atZone(ZoneOffset.UTC).toLocalDate(),
            totalProfit
        )
    }
}
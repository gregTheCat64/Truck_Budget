package ru.javacat.data.db.models

import ru.javacat.common.utils.toLocalDate
import ru.javacat.common.utils.toMonth
import ru.javacat.domain.models.MonthlyProfit
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset

data class DbMonthlyProfit (
    val monthDate: String,
    val totalProfit: Long
) {
    fun toMonthlyProfitModel(): MonthlyProfit{
        return MonthlyProfit(
            monthDate.toMonth(),
            totalProfit
        )
    }
}
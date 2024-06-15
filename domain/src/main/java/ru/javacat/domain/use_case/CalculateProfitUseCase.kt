package ru.javacat.domain.use_case

import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateProfitUseCase @Inject constructor() {
    operator fun invoke(
        otherExpenses: Int,
        routeDuration: Int,
        payPerDiem: Int,
        fuelUsedUp: Int,
        fuelPrice: Float,
        revenue: Int,
        salary: Int
    ): Int{
        return (revenue - (salary + (fuelPrice * fuelUsedUp) + (payPerDiem * routeDuration) + otherExpenses)).roundToInt()
    }
}
package ru.javacat.domain.use_case

import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateTruckDriverSalaryUseCase @Inject constructor () {
    operator fun invoke(
        otherExpenses: Int,
        routeDuration: Int,
        payPerDiem: Int,
        fuelUsedUp: Int,
        fuelPrice: Float,
        revenue: Int,
        ): Int{
        return ((revenue - ((fuelUsedUp*fuelPrice)+(routeDuration*payPerDiem)+otherExpenses))/5).roundToInt()
    }
}
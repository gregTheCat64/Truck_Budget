package ru.javacat.domain.use_case

import ru.javacat.domain.models.Route
import ru.javacat.domain.models.SalaryCountMethod
import javax.inject.Inject

class CalculateTruckDriverSalaryUseCase @Inject constructor () {
    operator fun invoke(
        salaryCountMethod: SalaryCountMethod,
        revenue: Int,
        extraExpenses: Int,
        fuelPrice: Float,
        fuelUsedUp: Int,
        routeDuration: Int,
        costPerDiem: Int,
        profitPercentage: Int,
        revenuePercentage: Int,
        roadFee: Int,
        routeDistance: Int,
        costPerKilometer: Float
                        ): Float {
        return when (salaryCountMethod){
            SalaryCountMethod.BY_PROFIT -> {
                (revenue - extraExpenses - (fuelPrice * fuelUsedUp) -
                        (routeDuration * costPerDiem)  - roadFee) *
                        profitPercentage/100
            }
            SalaryCountMethod.BY_DISTANCE -> {
                routeDistance * costPerKilometer
            }
            SalaryCountMethod.BY_REVENUE -> {
                (revenue * revenuePercentage/100).toFloat()
            }
        }
    }
}
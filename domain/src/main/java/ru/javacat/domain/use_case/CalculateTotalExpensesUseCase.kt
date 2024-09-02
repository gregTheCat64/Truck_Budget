package ru.javacat.domain.use_case

import javax.inject.Inject

class CalculateTotalExpensesUseCase @Inject constructor() {
    operator fun invoke(driverSalary: Float,
                        fuelUsedUp: Int,
                        fuelPrice: Float,
                        routeDuration: Int,
                        costPerDiem: Int,
                        extraPoints: Int,
                        extraPointsCost: Int,
                        extraExpenses: Int,
                        contractorsCost: Int,
                        roadFee: Int
                        ): Float {
        return driverSalary +
                (fuelUsedUp * fuelPrice) +
                (routeDuration * costPerDiem) +
                (extraPoints * extraPointsCost) +
                extraExpenses + contractorsCost + roadFee
    }
}
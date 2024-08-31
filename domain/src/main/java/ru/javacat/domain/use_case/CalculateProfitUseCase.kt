package ru.javacat.domain.use_case

import ru.javacat.domain.models.Route
import javax.inject.Inject
import kotlin.math.roundToInt

class CalculateProfitUseCase @Inject constructor() {
    operator fun invoke(revenue: Int, totalExpenses: Float): Float {
        return revenue - totalExpenses
    }
}
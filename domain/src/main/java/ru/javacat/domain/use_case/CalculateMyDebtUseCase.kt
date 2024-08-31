package ru.javacat.domain.use_case

import ru.javacat.domain.models.Route
import javax.inject.Inject

class CalculateMyDebtUseCase @Inject constructor() {
    operator fun invoke(prepayment: Int, totalExpenses: Float): Float {
        return prepayment - totalExpenses
    }
}
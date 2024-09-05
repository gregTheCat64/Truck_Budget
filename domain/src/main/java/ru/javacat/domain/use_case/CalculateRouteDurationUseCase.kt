package ru.javacat.domain.use_case

import ru.javacat.domain.models.Route
import java.time.Period
import javax.inject.Inject

class CalculateRouteDurationUseCase @Inject constructor() {
    operator fun invoke(route: Route): Int {
        val firstDate = route.orderList.firstOrNull()?.points?.first()?.arrivalDate
        val lastDate = route.orderList.lastOrNull()?.points?.last()?.arrivalDate

        val period = Period.between(firstDate, lastDate)
        return period.days
    }
}
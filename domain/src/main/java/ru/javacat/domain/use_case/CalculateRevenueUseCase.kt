package ru.javacat.domain.use_case

import ru.javacat.domain.models.Route
import javax.inject.Inject

class CalculateRevenueUseCase @Inject constructor() {
    operator fun invoke(route: Route): Int {
        var sumRevenue = 0
        val orders = route.orderList
        for (i in orders) {
            sumRevenue = i.price?.let { sumRevenue.plus(it) }?:0
        }
        return sumRevenue
    }
}
package ru.javacat.domain.use_case

import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository

class InsertOrder(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(route: Route, order: Order){
        repository.insertOrder(route, order)
    }
}
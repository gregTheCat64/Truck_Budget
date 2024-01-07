package ru.javacat.domain.use_case

import ru.javacat.domain.models.Order
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.Repository

class InsertOrder(
    private val repository: Repository
) {
    suspend operator fun invoke(route: Route, order: Order){
        repository.insertOrder(route, order)
    }
}
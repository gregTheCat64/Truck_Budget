package ru.javacat.domain.use_case

import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.RouteRepository

class InsertRoute(
    private val repository: RouteRepository,
) {
    suspend operator fun invoke(route: Route){
        repository.insertRoute(route)
    }
}
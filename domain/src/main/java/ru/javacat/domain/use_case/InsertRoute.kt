package ru.javacat.domain.use_case

import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.Repository

class InsertRoute(
    private val repository: Repository,
) {
    suspend operator fun invoke(route: Route){
        repository.insertRoute(route)
    }
}
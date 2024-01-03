package ru.javacat.domain.use_case

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Route
import ru.javacat.domain.repo.Repository

class GetRoutes(
    private val repository: Repository
) {
    operator fun invoke(): Flow<List<Route?>>{
        return repository.allRoutes
    }
}
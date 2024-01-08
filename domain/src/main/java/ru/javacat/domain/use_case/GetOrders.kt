package ru.javacat.domain.use_case

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Order
import ru.javacat.domain.repo.RouteRepository

class GetOrders(
    private val repository: RouteRepository
) {
    operator fun invoke(): Flow<List<Order?>>{
        return repository.allOrders
    }
}
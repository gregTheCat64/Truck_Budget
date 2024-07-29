package ru.javacat.domain.use_case

import ru.javacat.domain.repo.OrderRepository

class GetOrders(
    private val repository: OrderRepository
) {
//    operator fun invoke(): Flow<List<Order?>>{
//        return repository.allOrders
//    }
}
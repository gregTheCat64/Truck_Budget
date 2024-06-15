package ru.javacat.domain.use_case

import ru.javacat.domain.repo.OrderRepository
import javax.inject.Inject

class ClearEditedOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun  invoke(){
        orderRepository.clearCurrentOrder()
    }
}
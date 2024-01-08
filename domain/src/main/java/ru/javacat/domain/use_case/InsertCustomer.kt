package ru.javacat.domain.use_case

import ru.javacat.domain.models.Customer
import ru.javacat.domain.repo.RouteRepository

class InsertCustomer(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(customer: Customer){
        repository.insertCustomer(customer)
    }
}
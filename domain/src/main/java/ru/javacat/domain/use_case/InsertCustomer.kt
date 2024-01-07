package ru.javacat.domain.use_case

import ru.javacat.domain.models.Customer
import ru.javacat.domain.repo.Repository

class InsertCustomer(
    private val repository: Repository
) {
    suspend operator fun invoke(customer: Customer){
        repository.insertCustomer(customer)
    }
}
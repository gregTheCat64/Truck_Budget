package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Customer

interface CustomerRepository: BaseChooseItemRepository<Customer, String, Long>{
    val customers: Flow<List<Customer>>
}
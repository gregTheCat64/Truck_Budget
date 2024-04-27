package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee

interface CustomerRepository: BaseCrud<Customer, String, Long>{
    val chosenCustomer: StateFlow<Customer?>

    val customers: Flow<List<Customer>>

}
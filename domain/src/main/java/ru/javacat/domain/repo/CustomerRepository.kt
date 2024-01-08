package ru.javacat.domain.repo

import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee

interface CustomerRepository {
    suspend fun insertCustomer(customer: Customer)

    suspend fun insertEmployee(employee: Employee)
}
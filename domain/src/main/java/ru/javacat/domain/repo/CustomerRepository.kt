package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Customer
import ru.javacat.domain.models.Employee

interface CustomerRepository {

    suspend fun getCustomers(): List<Customer>
    suspend fun searchCustomers(search: String): List<Customer>
    suspend fun getEmployeesByCustomerId(customerId: String): List<Employee>
    suspend fun insertCustomer(customer: Customer)

    suspend fun insertEmployee(employee: Employee)
}
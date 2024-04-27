package ru.javacat.domain.repo

import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Employee

interface EmployeesRepository: BaseCrud<Employee, String, Long> {
    val chosenEmployee: StateFlow<Employee?>
    suspend fun getEmployeesByCustomerId(customerId: Long): List<Employee>
}
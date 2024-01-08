package ru.javacat.domain.use_case

import ru.javacat.domain.models.Employee
import ru.javacat.domain.repo.RouteRepository

class InsertEmployee(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(employee: Employee){
        repository.insertEmployee(employee)
    }
}
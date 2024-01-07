package ru.javacat.domain.use_case

import ru.javacat.domain.models.Employee
import ru.javacat.domain.repo.Repository

class InsertEmployee(
    private val repository: Repository
) {
    suspend operator fun invoke(employee: Employee){
        repository.insertEmployee(employee)
    }
}
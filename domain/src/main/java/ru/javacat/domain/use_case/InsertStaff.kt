package ru.javacat.domain.use_case

import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.RouteRepository

class InsertStaff(
    private val repository: RouteRepository
) {
    suspend operator fun invoke(staff: Staff){
        repository.insertDriver(staff)
    }
}
package ru.javacat.domain.use_case

import ru.javacat.domain.models.Staff
import ru.javacat.domain.repo.Repository

class InsertStaff(
    private val repository: Repository
) {
    suspend operator fun invoke(staff: Staff){
        repository.insertDriver(staff)
    }
}
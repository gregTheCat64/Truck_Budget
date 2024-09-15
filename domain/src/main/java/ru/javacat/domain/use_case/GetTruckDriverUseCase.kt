package ru.javacat.domain.use_case

import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

class GetTruckDriverUseCase @Inject constructor(
    private val repository: TruckDriversRepository
) {
    suspend fun invoke(id: Long) = repository.getById(id)
}
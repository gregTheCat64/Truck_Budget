package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

class GetTruckUseCase @Inject constructor(
    private val repository: TrucksRepository
) {
    suspend fun invoke(id: Long) = repository.getById(id)
}
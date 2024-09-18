package ru.javacat.domain.use_case

import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

class SaveNewTruckUseCase @Inject constructor(
    private val repository: TrucksRepository
) {
    suspend fun invoke(truck: Truck) = repository.insert(truck)
}
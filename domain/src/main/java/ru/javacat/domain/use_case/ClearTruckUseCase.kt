package ru.javacat.domain.use_case

import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

class ClearTruckUseCase @Inject constructor(
    private val repository: TrucksRepository
) {
    suspend operator fun invoke (){
        repository.clearItem()
    }
}
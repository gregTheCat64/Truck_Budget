package ru.javacat.domain.use_case

import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

class SetTruckUseCase @Inject constructor(
    private val trucksRepository: TrucksRepository
) {
    suspend operator fun invoke (t: Truck){
        trucksRepository.setItem(t)
    }
}
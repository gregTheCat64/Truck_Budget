package ru.javacat.domain.use_case

import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

class SetTruckDriverUseCase @Inject constructor(
    private val truckDriversRepository: TruckDriversRepository
) {
    suspend fun invoke(t: TruckDriver){
        truckDriversRepository.setItem(t)
    }
}
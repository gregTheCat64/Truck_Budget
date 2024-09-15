package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

class HideTruckUseCase @Inject constructor(
    private val repository: TrucksRepository
) {
    suspend fun invoke(id:Long){
        val truckToHide = repository.getById(id)
        truckToHide?.copy(isHidden = true)?.let { repository.updateDriverToDb(it) }
    }
}
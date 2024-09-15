package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

class HideTruckDriverUseCase @Inject constructor(
    private val repository: TruckDriversRepository
) {
    suspend fun invoke(id: Long) {
        val driverToHide = repository.getById(id)
        driverToHide?.copy(isHidden = true)?.let { repository.updateDriverToDb(it) }
    }
}
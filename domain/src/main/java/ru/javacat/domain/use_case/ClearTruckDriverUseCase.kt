package ru.javacat.domain.use_case

import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

class ClearTruckDriverUseCase @Inject constructor(
    private val repository: TruckDriversRepository
) {
    suspend operator fun invoke (){
        repository.clearItem()
    }
}
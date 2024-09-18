package ru.javacat.domain.use_case

import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.TruckDriver
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

class SaveNewTruckDriverUseCase @Inject constructor(
    private val repository: TruckDriversRepository
) {
    suspend fun invoke(td: TruckDriver) = repository.insert(td)
}
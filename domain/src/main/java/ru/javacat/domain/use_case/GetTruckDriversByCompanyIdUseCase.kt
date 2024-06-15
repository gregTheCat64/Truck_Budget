package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TruckDriversRepository
import javax.inject.Inject

class GetTruckDriversByCompanyIdUseCase @Inject constructor(
    private val truckDriversRepository: TruckDriversRepository
) {
    suspend operator fun invoke(id: Long) = truckDriversRepository.getByCompanyId(id)
}
package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

class GetTrucksByCompanyIdUseCase @Inject constructor(
    private val trucksRepository: TrucksRepository
) {
    suspend operator fun invoke(id: Long) = trucksRepository.getByCompanyId(id)
}
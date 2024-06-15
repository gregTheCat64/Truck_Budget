package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TrailersRepository
import javax.inject.Inject

class GetTrailersByCompanyIdUseCase @Inject constructor(
    private val trailersRepository: TrailersRepository
) {
    suspend operator fun invoke(id: Long) = trailersRepository.getByCompanyId(id)
}
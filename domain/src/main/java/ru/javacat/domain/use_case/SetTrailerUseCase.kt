package ru.javacat.domain.use_case

import ru.javacat.domain.models.Trailer
import ru.javacat.domain.repo.TrailersRepository
import javax.inject.Inject

class SetTrailerUseCase @Inject constructor(
    private val trailersRepository: TrailersRepository
) {
    suspend operator fun invoke(t: Trailer){
        trailersRepository.setItem(t)
    }
}
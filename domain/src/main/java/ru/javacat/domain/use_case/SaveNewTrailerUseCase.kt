package ru.javacat.domain.use_case

import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck
import ru.javacat.domain.repo.TrailersRepository
import ru.javacat.domain.repo.TrucksRepository
import javax.inject.Inject

class SaveNewTrailerUseCase @Inject constructor(
    private val repository: TrailersRepository
) {
    suspend fun invoke(trailer: Trailer) = repository.insert(trailer)
}
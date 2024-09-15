package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TrailersRepository
import javax.inject.Inject

class GetTrailerUseCase @Inject constructor(
    private val repository: TrailersRepository
) {
    suspend fun invoke(id: Long) = repository.getById(id)
}
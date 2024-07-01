package ru.javacat.domain.use_case

import ru.javacat.domain.models.Trailer
import ru.javacat.domain.repo.TrailersRepository
import javax.inject.Inject

class ClearTrailerUseCase @Inject constructor(
    private val repository: TrailersRepository
) {
    suspend operator fun invoke (){
        repository.clearItem()
    }
}
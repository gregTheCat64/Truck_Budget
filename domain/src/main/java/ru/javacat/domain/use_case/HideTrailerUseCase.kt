package ru.javacat.domain.use_case

import ru.javacat.domain.repo.TrailersRepository
import javax.inject.Inject

class HideTrailerUseCase @Inject constructor(
    private val repository: TrailersRepository
) {
    suspend fun  invoke(id: Long){
        val trailerToHide = repository.getById(id)
        trailerToHide?.copy(isHidden = true)?.let { repository.updateTrailerToDb(it) }
    }
}
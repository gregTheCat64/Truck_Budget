package ru.javacat.domain.repo

import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Trailer

interface TrailersRepository: BaseCrud<Trailer, String, Long> {
    val chosenTrailer: StateFlow<Trailer?>
}
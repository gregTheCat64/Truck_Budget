package ru.javacat.domain.repo

import ru.javacat.domain.models.Trailer

interface TrailersRepository: BaseChooseItemRepository<Trailer, String, Long> {
    //val chosenTrailer: StateFlow<Trailer?>
}
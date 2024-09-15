package ru.javacat.domain.repo

import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck

interface TrailersRepository: BaseChooseItemRepository<Trailer, String, Long> {
    val chosenTrailer: StateFlow<Trailer?>
    suspend fun getByCompanyId(companyId: Long):  List<Trailer>?

    suspend fun createDefaultTrailer()

    suspend fun updateTrailerToDb(trailer: Trailer)
}
package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.TrailersDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.repo.TrailersRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrailersRepositoryImpl @Inject constructor(
    val dao: TrailersDao
): TrailersRepository {

    private val _chosenTrailer = MutableStateFlow<Trailer?>(null)
    override val chosenTrailer: StateFlow<Trailer?>
        get() = _chosenTrailer.asStateFlow()

    override suspend fun getAll(): List<Trailer> {
        return dbQuery { dao.getAll().map { it.toTrailer() } }
    }

    override suspend fun getById(id: Long): Trailer {
        return dbQuery { dao.getById(id).toTrailer() }
    }

    override suspend fun createDefaultTrailer(){
        if (getByCompanyId(-1).isEmpty()){
            Log.i("trailerRepo", "creating default trailer")
            val defaultTrailer = Trailer(
                0,
                -1,
                "МойПрицеп",
                regionCode = 0
            )
            dbQuery { dao.insert(defaultTrailer.toDb()) }
        }
    }

    override suspend fun removeById(id: Long) {
        dbQuery { dao.remove(id) }
    }

    override suspend fun getByCompanyId(companyId: Long): List<Trailer> {
        return dbQuery { dao.getByCompanyId(companyId).map { it.toTrailer() } }
    }

    override suspend fun search(s: String): List<Trailer> {
        return dbQuery { dao.searchTrailers(s).map { it.toTrailer() } }
    }

    override suspend fun insert(t: Trailer): Long {
       return dbQuery { dao.insert(t.toDb()) }
    }

    override suspend fun setItem(t: Trailer) {
        _chosenTrailer.emit(t)
    }

    override suspend fun clearItem() {
        _chosenTrailer.emit(null)
    }
}
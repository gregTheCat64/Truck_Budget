package ru.javacat.data.impl

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.javacat.data.db.dao.CompaniesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompaniesRepositoryImpl @Inject constructor(
    private val dao: CompaniesDao
): CompaniesRepository {

//    override val chosenItem: StateFlow<Company?>
//        get() = _chosenCustomer.asStateFlow()
//    private val _chosenCustomer = MutableStateFlow<Company?>(null)

    override suspend fun getAll(): List<Company> {
        return dbQuery { dao.getCustomers().map { it.toCompanyModel() } }
    }

    override suspend fun createDefaultCompany() {
        if (getAll().isEmpty()){
            Log.i("companiesRepo", "creating default Company")
            val defaultCompany = Company(
                -1,
                "Моя Компания"
            )
            dbQuery { dao.insertCustomer(defaultCompany.toDb()) }
        }
    }

    override suspend fun getById(id: Long): Company? {
        return dbQuery { dao.getById(id)?.toCompanyModel()}
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun search(s: String): List<Company> {
        return dbQuery { dao.searchCustomers(s).map { it.toCompanyModel() } }
    }

    override suspend fun insert(t: Company): Long {
        return dbQuery { dao.insertCustomer(t.toDb()) }
    }

    override suspend fun setItem(t: Company) {
        //_chosenCustomer.emit(t)
    }

    override suspend fun clearItem() {
        //_chosenCustomer.emit(null)
    }
}
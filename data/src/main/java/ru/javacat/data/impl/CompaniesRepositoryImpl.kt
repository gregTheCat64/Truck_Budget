package ru.javacat.data.impl

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.javacat.data.db.dao.CompaniesDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.data.switchDatabaseModified
import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompaniesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dao: CompaniesDao
): CompaniesRepository {

    private val TAG = "CompaniesRepoImpl"

    override suspend fun getAll(): List<Company> {
        Log.i(TAG, "getAll")
        val result = dbQuery { dao.getAll().map { it.toCompanyModel() } }
        Log.i(TAG, "result is $result")
        return result
    }

    override suspend fun createDefaultCompany() {
        if (getAll().isEmpty()){
            Log.i("companiesRepo", "creating default Company")
            val defaultCompany = Company(
                id = -1,
                nameToShow = "Моя Компания",
                isFavorite = true
            )
            dbQuery { dao.insertCustomer(defaultCompany.toDb()) }
        }
    }

    override suspend fun getById(id: Long): Company? {
        return dbQuery { dao.getById(id)?.toCompanyModel()}
    }

    override suspend fun updateCompanyToDb(company: Company) {
        switchDatabaseModified(context, true)
        dbQuery { dao.updateCompany(company.toDb()) }
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun search(s: String): List<Company> {
        return dbQuery { dao.searchCustomers(s).map { it.toCompanyModel() } }
    }

    override suspend fun insert(t: Company): Long {
        return dbQuery {
            switchDatabaseModified(context, true)
            dao.insertCustomer(t.toDb()) }
    }

    override suspend fun setItem(t: Company) {
        //_chosenCustomer.emit(t)
    }

    override suspend fun clearItem() {
        //_chosenCustomer.emit(null)
    }
}
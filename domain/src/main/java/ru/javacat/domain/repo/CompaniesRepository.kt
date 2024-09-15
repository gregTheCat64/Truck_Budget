package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Company

interface CompaniesRepository: BaseChooseItemRepository<Company, String, Long>{
    //val customers: Flow<List<Company>>
    suspend fun createDefaultCompany()

    suspend fun updateCompanyToDb(company: Company)
}
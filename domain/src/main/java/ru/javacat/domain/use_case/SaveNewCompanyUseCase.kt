package ru.javacat.domain.use_case

import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject

class SaveNewCompanyUseCase @Inject constructor(
    private val repository: CompaniesRepository
) {
    suspend operator fun invoke(company: Company) =
        repository.insert(company)
}
package ru.javacat.domain.use_case

import ru.javacat.domain.models.Company
import ru.javacat.domain.repo.CompaniesRepository
import javax.inject.Inject

class GetCompaniesUseCase @Inject constructor(
    private val companiesRepository: CompaniesRepository
) {
    suspend operator fun invoke() = companiesRepository.getAll()

}
package ru.javacat.domain.repo

import kotlinx.coroutines.flow.Flow
import ru.javacat.domain.models.Partner

interface CustomerRepository: BaseChooseItemRepository<Partner, String, Long>{
    val customers: Flow<List<Partner>>
}
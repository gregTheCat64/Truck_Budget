package ru.javacat.domain.repo

import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Truck

interface TrucksRepository: BaseChooseItemRepository<Truck, String, Long>{

    val chosenTruck: StateFlow<Truck?>

    suspend fun getByCompanyId(companyId: Long): List<Truck>

    suspend fun createDefaultTruck()
}
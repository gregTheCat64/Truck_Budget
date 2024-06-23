package ru.javacat.domain.repo

import ru.javacat.domain.models.Truck
import ru.javacat.domain.models.TruckDriver

interface TruckDriversRepository: BaseChooseItemRepository<TruckDriver, String, Long>{
    //val chosenDriver: StateFlow<TruckDriver?>
    suspend fun getByCompanyId(companyId: Long):  List<TruckDriver>
}
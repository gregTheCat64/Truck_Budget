package ru.javacat.domain.repo

import ru.javacat.domain.models.TruckDriver

interface TruckDriversRepository: BaseChooseItemRepository<TruckDriver, String, String>{
    //val chosenDriver: StateFlow<TruckDriver?>
}
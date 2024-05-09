package ru.javacat.domain.repo

import ru.javacat.domain.models.Truck

interface TrucksRepository: BaseChooseItemRepository<Truck, String, Long>{
    //val chosenTruck: StateFlow<Truck?>
}
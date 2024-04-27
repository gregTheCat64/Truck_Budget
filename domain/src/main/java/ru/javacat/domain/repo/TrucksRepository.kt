package ru.javacat.domain.repo

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import ru.javacat.domain.models.Truck

interface TrucksRepository: BaseCrud<Truck, String, Long>{
    val chosenTruck: StateFlow<Truck?>
}
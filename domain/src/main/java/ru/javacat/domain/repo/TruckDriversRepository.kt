package ru.javacat.domain.repo

import kotlinx.coroutines.flow.StateFlow

interface TruckDriversRepository: BaseCrud<TruckDriver, String, String>{
    val chosenDriver: StateFlow<TruckDriver?>
}
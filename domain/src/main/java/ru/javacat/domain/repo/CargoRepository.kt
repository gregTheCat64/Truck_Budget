package ru.javacat.domain.repo

import ru.javacat.domain.models.CargoName

interface CargoRepository: BaseCrud<CargoName, String> {

//    suspend fun searchCargos(search: String): List<Cargo>
//    suspend fun getCargos(): List<Cargo>
//    suspend fun insertCargo(cargo: Cargo)
}
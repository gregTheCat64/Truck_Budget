package ru.javacat.domain.repo

import ru.javacat.domain.models.Cargo

interface CargoRepository: BaseCrud<Cargo> {

//    suspend fun searchCargos(search: String): List<Cargo>
//    suspend fun getCargos(): List<Cargo>
//    suspend fun insertCargo(cargo: Cargo)
}
package ru.javacat.domain.models

data class Cargo (
    val cargoWeight: Int? = null,
    val cargoVolume: Int? = null,
    val cargoName: String? = null,

    val isBackLoad: Boolean = true,
    val isSideLoad: Boolean = false,
    val isTopLoad: Boolean = false
)
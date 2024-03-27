package ru.javacat.domain.models

data class Cargo (
    val cargoWeight: Int,
    val cargoVolume: Int,
    val cargoName: String,

    val isBackLoad: Boolean = true,
    val isSideLoad: Boolean,
    val isTopLoad: Boolean
)
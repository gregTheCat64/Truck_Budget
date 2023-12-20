package ru.javacat.domain.models

data class Vehicle (
    val regNumber: String,
    val vin: String?,
    val model: String?,
    val type: String?,
    val yearOfManufacturing: Int,
)

package ru.javacat.domain.models

data class Vehicle (
    val id: String,
    val regNumber: String,
    val vin: String?,
    val model: String?,
    val type: String?,
    val yearOfManufacturing: Int,
)

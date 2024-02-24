package ru.javacat.domain.models

data class Location(
    val id: Long? = null,
    val name: String,
    val positionId: Long = 0L,
)

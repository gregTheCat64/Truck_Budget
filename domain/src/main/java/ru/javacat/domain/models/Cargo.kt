package ru.javacat.domain.models

data class Cargo(
    val id: Long? = null,
    val name: String,
    val positionId: Long = 0L
)

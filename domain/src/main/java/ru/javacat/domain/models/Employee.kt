package ru.javacat.domain.models

data class Employee(
    val id: Int,
    val customerAtiNumber: Int,
    val name: String,
    val phoneNumber: Long,
    val secondNumber: Long?,
    val email: String?
)

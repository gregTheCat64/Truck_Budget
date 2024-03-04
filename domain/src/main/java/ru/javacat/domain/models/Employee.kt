package ru.javacat.domain.models

data class Employee(
    val id: Long = 0L,
    val name: String,
    val customerId: String,
    val phoneNumber: String,
    val secondNumber: String?,
    val email: String?,
    val comment: String?,
)

package ru.javacat.domain.models

data class Employee(
    val id: Int,
    val customerId: String,
    val name: String,
    val phoneNumber: String,
    val secondNumber: String?,
    val email: String?,
    val comment: String?
)

package ru.javacat.data.db.entities

import androidx.room.Entity

@Entity
data class DbEmployee(
    val name: String,
    val phoneNumber: String,
    val secondNumber: String?,
    val email: String
)

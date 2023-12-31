package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "locations_table"
)
data class DbLocation(
    @PrimaryKey
    val name: String
)

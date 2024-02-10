package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Location

@Entity(
    tableName = "locations_table"
)
data class DbLocation(
    @PrimaryKey()
    val id: String,
    val name: String
) {
    fun toLocationModel() = Location(
        id, name
    )
}

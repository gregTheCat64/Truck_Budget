package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Location

@Entity(
    tableName = "locations_table"
)
data class DbLocation(
    @PrimaryKey()
    val id: Long?,
    val name: String,
    val positionId: Long
) {
    fun toLocationModel() = Location(
        id, name, positionId
    )
}

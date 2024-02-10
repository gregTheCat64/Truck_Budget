package ru.javacat.data.db.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Cargo

@Entity(
    tableName = "cargo_table"
)
data class DbCargo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val positionId: Int,
    val name: String
) {
    fun toCargoModel() = Cargo(
        id, positionId, name
    )
}
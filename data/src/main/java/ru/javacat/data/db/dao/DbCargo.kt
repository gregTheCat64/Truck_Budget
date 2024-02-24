package ru.javacat.data.db.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Cargo

@Entity(
    tableName = "cargo_table"
)
data class DbCargo(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String,
    val positionId: Long,
) {
    fun toCargoModel() = Cargo(
        id, name, positionId
    )
}
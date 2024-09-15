package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.CargoName

@Entity(
    tableName = "cargo_table"
)
data class DbCargo(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String,
    val isHidden: Boolean,
    val positionId: Long,
) {
    fun toCargoModel() = CargoName(
        id, name,isHidden,  positionId
    )
}
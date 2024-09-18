package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Trailer
import ru.javacat.domain.models.Truck

@Entity(tableName = "trailers_table")
data class DbTrailer  (
@PrimaryKey(autoGenerate = true)
val id: Long,
val isHidden: Boolean,
val companyId: Long,
val regNumber: String,
val regionCode: Int?,
val vin: String?,
val model: String?,
val type: String?,
val yearOfManufacturing: String?,
){
    fun toTrailer() = Trailer(
        id, isHidden, companyId,  regNumber,regionCode, vin, model,  yearOfManufacturing, type
    )
}
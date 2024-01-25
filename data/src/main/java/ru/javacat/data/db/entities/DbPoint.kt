package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.javacat.common.utils.toLocalDate
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point

@Entity(
    tableName = "points_table",
    foreignKeys = [
        ForeignKey(
            entity = DbOrder::class,
            parentColumns = ["id"],
            childColumns = ["orderId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class DbPoint(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(index = true)
    val orderId: String,
    @Embedded
    val location: Location,
    val arrivalDate: String
) {
    fun toPointModel() = Point(
        id, location, arrivalDate.toLocalDate()
    )
}

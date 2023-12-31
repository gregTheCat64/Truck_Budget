package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.javacat.domain.models.Location

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
    val orderId: Int,
    @Embedded
    val location: Location,
    val arrivalDate: String
)

package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
    val orderId: String,
    val location: String,
    val arrivalDate: String
)

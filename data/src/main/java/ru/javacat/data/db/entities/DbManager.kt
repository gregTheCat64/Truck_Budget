package ru.javacat.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.common.utils.toLocalDate
import ru.javacat.domain.models.Manager

@Entity(tableName = "managers_table")
data class DbManager(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val positionId: Long,
    @ColumnInfo(index = true)
    val customerId: Long,
    val firstName: String,
    val middleName: String?,
    val surName: String?,
    val passportNumber: String?,
    val passportReceivedDate: String?,
    val passportReceivedPlace: String?,
    val placeOfRegistration: String?,
    val phoneNumber: String?,
    val secondNumber: String?,
    val email: String?,
    val comment: String?
) {
    fun toManagerModel() = Manager(
        id,
        positionId,
        customerId,
        firstName,
        middleName,
        surName,
        passportNumber,
        passportReceivedDate?.toLocalDate(),
        passportReceivedPlace,
        placeOfRegistration,
        phoneNumber,
        secondNumber,
        email,
        comment
    )
}

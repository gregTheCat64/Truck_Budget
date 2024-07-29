package ru.javacat.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.javacat.common.utils.toLocalDate
import ru.javacat.domain.models.Expense
import java.time.LocalDate

@Entity(
    tableName = "expenses_table"
)
data class DbExpense(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val description: String,
    val date: String,
    val price: Int
) {
    fun toExpenseModel() = Expense(
        id, name, description, date.toLocalDate(), price
    )
}
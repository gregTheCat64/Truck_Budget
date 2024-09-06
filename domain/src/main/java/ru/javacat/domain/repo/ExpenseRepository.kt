package ru.javacat.domain.repo

import ru.javacat.domain.models.Expense
import ru.javacat.domain.models.MonthlyProfit
import java.time.Year

interface ExpenseRepository: BaseChooseItemRepository<Expense, String, Long> {

    suspend fun getAllExpensesByYear(year: Int): List<Expense>

    suspend fun getMonthlyExpenseByYear(year: String): List<MonthlyProfit>
}
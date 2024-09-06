package ru.javacat.data.impl

import kotlinx.coroutines.flow.StateFlow
import ru.javacat.data.db.dao.ExpenseDao
import ru.javacat.data.db.mappers.toDb
import ru.javacat.data.dbQuery
import ru.javacat.domain.models.Expense
import ru.javacat.domain.models.MonthlyProfit
import ru.javacat.domain.repo.ExpenseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
): ExpenseRepository {
//    override val chosenItem: StateFlow<Expense?>
//        get() = TODO("Not yet implemented")

    override suspend fun getAll(): List<Expense> {
        return dbQuery { dao.getExpenses().map { it.toExpenseModel() } }
    }

    override suspend fun getAllExpensesByYear(year: Int): List<Expense> {
        return dbQuery { dao.getAllExpensesByYear(year.toString()).map { it.toExpenseModel() } }
    }

    override suspend fun getById(id: Long): Expense? {
        return dbQuery { dao.getExpenseById(id).toExpenseModel() }
    }

    override suspend fun getMonthlyExpenseByYear(year: String): List<MonthlyProfit> {
        return dbQuery { dao.getMonthlyExpenseByYear(year).map {
            it.toMonthlyProfitModel()
        } }
    }

    override suspend fun removeById(id: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun search(s: String): List<Expense> {
        return dbQuery { dao.searchExpense(s).map { it.toExpenseModel() } }
    }

    override suspend fun insert(t: Expense): Long {
       return dbQuery { dao.insertExpense(t.toDb()) }
    }

    override suspend fun setItem(t: Expense) {
        TODO("Not yet implemented")
    }

    override suspend fun clearItem() {
        TODO("Not yet implemented")
    }
}
package ru.javacat.domain.repo

import ru.javacat.domain.models.Expense

interface ExpenseRepository: BaseChooseItemRepository<Expense, String, Long>
package ru.javacat.ui.adapters

import ru.javacat.domain.models.Employee
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter

class ChooseEmployeeAdapter(onItem: (Employee)-> Unit): BaseNameLongIdAdapter<Employee>(onItem) {
}
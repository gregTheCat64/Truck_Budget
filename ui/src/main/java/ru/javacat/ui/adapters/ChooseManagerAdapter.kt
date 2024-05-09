package ru.javacat.ui.adapters

import ru.javacat.domain.models.Manager
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter

class ChooseManagerAdapter(onItem: (Manager)-> Unit): BaseNameLongIdAdapter<Manager>(onItem) {
}
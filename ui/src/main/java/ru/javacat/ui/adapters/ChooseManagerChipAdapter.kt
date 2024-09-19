package ru.javacat.ui.adapters

import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Manager
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter

class ChooseManagerChipAdapter(override val onItem: (Manager) -> Unit): BaseNameLongIdAdapter<Manager>(onItem) {
}
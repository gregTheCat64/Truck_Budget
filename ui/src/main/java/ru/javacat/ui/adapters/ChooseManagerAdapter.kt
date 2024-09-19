package ru.javacat.ui.adapters

import ru.javacat.domain.models.Manager
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter

class ChooseManagerAdapter(
    override val onItem: (Manager) -> Unit
): BaseNameOneLineLongIdAdapter<Manager>(onItem)
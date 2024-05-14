package ru.javacat.ui.adapters

import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter


class ChooseDriverAdapter(
    override val onItem: (TruckDriver) -> Unit
): BaseNameOneLineLongIdAdapter<TruckDriver>(onItem)

package ru.javacat.ui.adapters

import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter


class ChooseDriverAdapter(
    override val onItem: (TruckDriver) -> Unit
): BaseNameLongIdAdapter<TruckDriver>(onItem)

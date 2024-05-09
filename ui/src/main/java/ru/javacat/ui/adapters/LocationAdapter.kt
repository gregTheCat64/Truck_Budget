package ru.javacat.ui.adapters

import ru.javacat.domain.models.Location
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter

class LocationAdapter(
    override val onItem: (Location) -> Unit
): BaseNameLongIdAdapter<Location>(onItem)
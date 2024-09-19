package ru.javacat.ui.adapters

import ru.javacat.domain.models.Location
import ru.javacat.ui.adapters.my_adapter.BaseNameAndRemoveLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.OnModelWithRemoveBtnListener

class LocationAdapter(
    onListener: OnModelWithRemoveBtnListener
): BaseNameAndRemoveLongIdAdapter<Location>(onListener)
package ru.javacat.ui.adapters

import ru.javacat.domain.models.CargoName
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter

class CargoAdapter(onItem: (CargoName) -> Unit): BaseNameLongIdAdapter<CargoName>(onItem)
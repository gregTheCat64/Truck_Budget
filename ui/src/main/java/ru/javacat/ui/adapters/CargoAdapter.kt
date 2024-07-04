package ru.javacat.ui.adapters

import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.CargoName
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter

class CargoAdapter(
    override val onItem: (CargoName) -> Unit
):BaseNameOneLineLongIdAdapter<CargoName>(onItem) {
}
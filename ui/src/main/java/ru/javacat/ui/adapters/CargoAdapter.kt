package ru.javacat.ui.adapters

import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.CargoName
import ru.javacat.ui.adapters.my_adapter.BaseNameAndRemoveLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.OnModelWithRemoveBtnListener

class CargoAdapter(
    onListener: OnModelWithRemoveBtnListener
):BaseNameAndRemoveLongIdAdapter<CargoName>(onListener) {
}
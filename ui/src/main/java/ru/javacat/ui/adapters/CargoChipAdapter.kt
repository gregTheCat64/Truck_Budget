package ru.javacat.ui.adapters

import ru.javacat.domain.models.CargoName
import ru.javacat.ui.adapters.my_adapter.BaseNameAndRemoveLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.OnModelWithRemoveBtnListener

class CargoChipAdapter( onListener: OnModelWithRemoveBtnListener): BaseNameAndRemoveLongIdAdapter<CargoName>(onListener)
package ru.javacat.ui.adapters

import ru.javacat.domain.models.Cargo
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter

class CargoAdapter(onItem: (Cargo) -> Unit): BaseNameLongIdAdapter<Cargo>(onItem)
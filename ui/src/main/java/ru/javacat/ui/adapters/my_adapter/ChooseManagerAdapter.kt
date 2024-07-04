package ru.javacat.ui.adapters.my_adapter

import ru.javacat.domain.models.Manager

class ChooseManagerAdapter(
    override val onItem: (Manager) -> Unit
):BaseNameOneLineLongIdAdapter<Manager>(onItem)
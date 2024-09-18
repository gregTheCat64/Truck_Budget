package ru.javacat.ui.adapters

import ru.javacat.domain.models.Trailer
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter


class ChooseTrailerAdapter(
    override val onItem: (Trailer) -> Unit
): BaseNameOneLineLongIdAdapter<Trailer>(onItem)
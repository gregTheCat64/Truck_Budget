package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Trailer
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.MyBaseAdapter
import ru.javacat.ui.databinding.NameItemOnelineBinding

class ChooseTrailerAdapter(
    override val onItem: (Trailer) -> Unit
): BaseNameOneLineLongIdAdapter<Trailer>(onItem)
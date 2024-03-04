package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.Staff
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.MyBaseAdapter
import ru.javacat.ui.databinding.NameItemOnelineBinding


class ChooseDriverAdapter(
    override val onItem: (Staff) -> Unit
): BaseNameLongIdAdapter<Staff>(onItem)

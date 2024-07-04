package ru.javacat.ui.adapters

import ru.javacat.domain.models.Company
import ru.javacat.ui.adapters.my_adapter.BaseNameOneLineLongIdAdapter

class ChooseCompanyAdapter(
    override val onItem: (Company) -> Unit
): BaseNameOneLineLongIdAdapter<Company>(onItem)
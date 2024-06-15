package ru.javacat.ui.adapters

import ru.javacat.domain.models.Company
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter


class ChooseCustomerAdapter(onItem: (Company)-> Unit): BaseNameLongIdAdapter<Company>(onItem)
package ru.javacat.ui.adapters

import ru.javacat.domain.models.Partner
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter


class ChooseCustomerAdapter(onItem: (Partner)-> Unit): BaseNameLongIdAdapter<Partner>(onItem)
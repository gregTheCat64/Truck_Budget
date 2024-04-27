package ru.javacat.ui.adapters

import ru.javacat.domain.models.Customer
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter


class ChooseCustomerAdapter(onItem: (Customer)-> Unit): BaseNameLongIdAdapter<Customer>(onItem)
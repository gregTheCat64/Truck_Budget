package ru.javacat.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.domain.models.BaseNameLongIdModel
import ru.javacat.domain.models.Customer
import ru.javacat.ui.adapters.my_adapter.BaseNameLongIdAdapter
import ru.javacat.ui.adapters.my_adapter.MyBaseAdapter
import ru.javacat.ui.databinding.NameItemOnelineBinding

class ChooseCustomerAdapter(onItem: (Customer)-> Unit): BaseNameLongIdAdapter<Customer>(onItem)
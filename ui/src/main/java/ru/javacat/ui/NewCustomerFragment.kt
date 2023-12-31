package ru.javacat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentNewCustomerBinding

class NewCustomerFragment:BaseFragment<FragmentNewCustomerBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewCustomerBinding
        get() = {inflater, container->
            FragmentNewCustomerBinding.inflate(inflater, container,false)
        }
}
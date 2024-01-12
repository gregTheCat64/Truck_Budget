package ru.javacat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentAddTransportBinding

class NewRouteFragment: BaseFragment<FragmentAddTransportBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddTransportBinding
        get() = {inflater, container->
            FragmentAddTransportBinding.inflate(inflater,container,false)
        }
}
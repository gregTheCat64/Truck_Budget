package ru.javacat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentNewRouteBinding

class NewRouteFragment: BaseFragment<FragmentNewRouteBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewRouteBinding
        get() = {inflater, container->
            FragmentNewRouteBinding.inflate(inflater,container,false)
        }
}
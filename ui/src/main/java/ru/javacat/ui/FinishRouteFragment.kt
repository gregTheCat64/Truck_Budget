package ru.javacat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentFinishRouteBinding

class FinishRouteFragment:BaseFragment<FragmentFinishRouteBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentFinishRouteBinding
        get() = {inflater, container->
            FragmentFinishRouteBinding.inflate(inflater, container,false)
        }
}
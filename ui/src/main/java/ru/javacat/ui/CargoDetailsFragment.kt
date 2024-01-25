package ru.javacat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentCargoDetailsBinding
import ru.javacat.ui.databinding.FragmentFinishRouteBinding

class CargoDetailsFragment: BaseFragment<FragmentCargoDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCargoDetailsBinding
        get() = {inflater, container->
            FragmentCargoDetailsBinding.inflate(inflater, container,false)
        }
}
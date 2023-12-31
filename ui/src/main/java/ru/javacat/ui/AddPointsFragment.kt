package ru.javacat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentAddPointsBinding

class AddPointsFragment: BaseFragment<FragmentAddPointsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddPointsBinding
        get() = {inflater, container->
            FragmentAddPointsBinding.inflate(inflater,container,false)
        }
}
package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentTransportBinding

class TransportFragment: BaseFragment<FragmentTransportBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTransportBinding
        get() = {inflater, container->
            FragmentTransportBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
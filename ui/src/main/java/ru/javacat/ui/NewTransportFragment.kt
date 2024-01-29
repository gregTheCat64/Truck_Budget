package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentNewTransportBinding

class NewTransportFragment: BaseFragment<FragmentNewTransportBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewTransportBinding
        get() = {inflater, container->
            FragmentNewTransportBinding.inflate(inflater, container, false)
        }


}
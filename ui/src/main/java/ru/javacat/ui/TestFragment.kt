package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentTestBinding

class TestFragment: BaseFragment<FragmentTestBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTestBinding
        get() = {inflater, container ->
            FragmentTestBinding.inflate(inflater,container,false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
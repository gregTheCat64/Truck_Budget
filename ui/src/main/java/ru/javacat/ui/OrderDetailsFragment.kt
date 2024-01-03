package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding

class OrderDetailsFragment:BaseFragment<FragmentOrderDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = {inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
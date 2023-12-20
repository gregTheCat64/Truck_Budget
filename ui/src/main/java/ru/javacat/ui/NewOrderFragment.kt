package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentNewOrderBinding

class NewOrderFragment:BaseFragment<FragmentNewOrderBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewOrderBinding
        get() = {inflater, container ->
            FragmentNewOrderBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
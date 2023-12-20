package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentAddEmployeeBinding

class AddEmployeeFragment: BaseFragment<FragmentAddEmployeeBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddEmployeeBinding =
        {inflater, container ->
            FragmentAddEmployeeBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}
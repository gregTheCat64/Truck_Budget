package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.databinding.FragmentExpenseListBinding

@AndroidEntryPoint
class ExpenseListFragment: BaseFragment<FragmentExpenseListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentExpenseListBinding
        get() = {inflater, container ->
            FragmentExpenseListBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
}
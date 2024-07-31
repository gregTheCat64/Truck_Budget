package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.ExpensesAdapter
import ru.javacat.ui.databinding.FragmentExpenseListBinding
import ru.javacat.ui.view_models.ExpenseListViewModel

@AndroidEntryPoint
class ExpenseListFragment: BaseFragment<FragmentExpenseListBinding>() {

    private val viewModel: ExpenseListViewModel by viewModels()
    private lateinit var expensesAdapter: ExpensesAdapter
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentExpenseListBinding
        get() = {inflater, container ->
            FragmentExpenseListBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expensesAdapter = ExpensesAdapter{

        }
        binding.expensesRecView.adapter = expensesAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.expenses.collectLatest {
                    expensesAdapter.submitList(it)
                }
            }
        }

    }
}
package ru.javacat.ui.expense

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentExpenseListBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class ExpenseListFragment: BaseFragment<FragmentExpenseListBinding>() {

    private val viewModel: ExpenseListViewModel by viewModels()
    private lateinit var expensesAdapter: ExpensesAdapter
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentExpenseListBinding
        get() = {inflater, container ->
            FragmentExpenseListBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getExpenseList()

        expensesAdapter = ExpensesAdapter{
            val bundle = Bundle().apply {
                putLong(FragConstants.EXPENSE_ID, it.id)
            }
            findNavController().navigate(R.id.editExpenseFragment, bundle)
        }
        binding.expensesRecView.adapter = expensesAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.expenses.collectLatest {
                    expensesAdapter.submitList(it)
                }
            }
        }

        binding.addExpenseBtn.setOnClickListener {
            findNavController().navigate(R.id.editExpenseFragment)
        }

    }
}
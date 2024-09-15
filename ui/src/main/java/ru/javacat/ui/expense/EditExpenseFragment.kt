package ru.javacat.ui.expense

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.asDayAndMonthFully
import ru.javacat.domain.models.Expense
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentEditExpenseBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.showCalendar
import java.lang.NumberFormatException
import java.time.LocalDate

@AndroidEntryPoint
class EditExpenseFragment: BaseFragment<FragmentEditExpenseBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentEditExpenseBinding
        get() = {inflater, container ->
            FragmentEditExpenseBinding.inflate(inflater, container, false)
        }

    private val viewModel: EditExpenseViewModel by viewModels()

    private var expenseIdArg: Long? = null
    private var needToRestore: Boolean = false

    private var name: String? = null
    private var description: String? = null
    private var cost: Int? = null
    private var date = LocalDate.now()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(FragConstants.EXPENSE_NAME){ _, bundle->
            val expenseName = bundle.getString(FragConstants.EXPENSE_NAME)
            expenseName?.let {
                binding.expenseTv.setText(it)
            }
        }

        expenseIdArg = arguments?.getLong(FragConstants.EXPENSE_ID)
        Log.i("EditExpenseFrag", "expenseId: $expenseIdArg")

        if (expenseIdArg != null) needToRestore = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }

                    R.id.save -> {
                        saveOrder()
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (needToRestore) {
            viewModel.getExpense(expenseIdArg!!)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedExpense.collectLatest {
                    it?.let { updateUi(it)}
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    when (it) {
                        LoadState.Success.GoBack -> {
                            findNavController().navigateUp()
                        }
                        LoadState.Error(this.toString()) ->{
                            Toast.makeText(requireContext(), "Saving Error", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }

        binding.expenseTv.setOnClickListener {
            changeName()
        }

        binding.expenseDateTv.setOnClickListener {
            parentFragmentManager.showCalendar {
                date = it
                binding.expenseDateTv.setText(it.toString())
            }
        }

        binding.removeBtn.setOnClickListener {
            expenseIdArg?.let { id -> viewModel.removeExpense(id) }
            Toast.makeText(requireContext(), getString(R.string.removed), Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeName(){
        val dialogFragment = ExpenseNameDialogFragment()
        dialogFragment.show(parentFragmentManager, "")
    }

    fun saveOrder(){
        //TODO Распространить на всё приложение!
        name = binding.expenseTv.text.toString().takeIf { it.isNotEmpty() }
        description = binding.DescriptionTv.text.toString().takeIf { it.isNotEmpty() }

        //TODO Распространить на всё приложение!
        try {
            if (binding.expensePriceTv.text.toString().isNotEmpty()){
                cost = binding.expensePriceTv.text.toString().toInt()
            }
        } catch (e: NumberFormatException){
            Toast.makeText(requireContext(), "Wrong number format", Toast.LENGTH_SHORT).show()
        }

        Log.i("EditExpenseFrag", "id = $expenseIdArg, name = $name, desc = $description, cost = $cost, date = $date")

        if (name!=null && cost!=null){
            viewModel.saveExpense(Expense(expenseIdArg?:0, name!!,false,0, description, date, cost!!))
        } else Toast.makeText(requireContext(), R.string.fill_requested_fields, Toast.LENGTH_SHORT).show()
    }

    private fun updateUi(expense: Expense){
        expense.apply {
            nameToShow.let {
                binding.expenseTv.setText(it)
            }
            description?.let {
                binding.DescriptionTv.setText(it)
            }
            price.let {
                binding.expensePriceTv.setText(it.toString())
            }
            date.let {
                binding.expenseDateTv.setText(it.toString())
            }
        }

        binding.removeBtn.isGone = false
    }
}
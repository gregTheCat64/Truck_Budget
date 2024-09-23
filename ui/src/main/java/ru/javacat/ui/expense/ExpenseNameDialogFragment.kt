package ru.javacat.ui.expense

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class ExpenseNameDialogFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseItemWithSearchBinding
    private lateinit var expenseNameAdapter: ExpenseNameAdapter
    private val viewModel: ExpenseNameDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseItemWithSearchBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getExpenseNames()
        addEditTextListener()

        binding.labelTv.text = getString(R.string.expense_name)
        binding.searchEditText.hint = "Введите название расхода"

        val expensesLayoutManager = FlexboxLayoutManager(requireContext())
        expensesLayoutManager.flexDirection = FlexDirection.ROW
        expensesLayoutManager.justifyContent = JustifyContent.FLEX_START
        binding.itemRecView.layoutManager = expensesLayoutManager


        expenseNameAdapter = ExpenseNameAdapter {
            //Toast.makeText(requireContext(), "pressed $it", Toast.LENGTH_SHORT).show()
            val bundle = Bundle().apply {
                putString(FragConstants.EXPENSE_NAME, it)
            }
            setFragmentResult(FragConstants.EXPENSE_NAME, bundle)
            this.dismiss()
        }
        binding.itemRecView.adapter = expenseNameAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.expenseNamesList.collectLatest {
                    expenseNameAdapter.submitList(it)
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            if (!binding.searchEditText.text.isNullOrEmpty()){
                val bundle = Bundle().apply {
                    putString(FragConstants.EXPENSE_NAME, binding.searchEditText.text.toString().trim())
                }
                setFragmentResult(FragConstants.EXPENSE_NAME, bundle)
                this.dismiss()
            }
        }
    }

    private fun addEditTextListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchExpense(p0.toString())
                binding.saveBtn.isVisible = !p0.isNullOrEmpty()
                binding.saveBtn.text = "Создать расход: $p0"
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}
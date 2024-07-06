package ru.javacat.ui.edit_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ChooseCompanyAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding

@AndroidEntryPoint
class EditCustomerDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemWithSearchBinding
    private lateinit var customersAdapter: ChooseCompanyAdapter
    private val viewModel: EditCustomerDialogViewModel by viewModels()

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

        viewModel.getCustomers()
        addEditTextListener()

        customersAdapter = ChooseCompanyAdapter {
            viewModel.addCustomerToOrder(it)
            this.dismiss()
        }
        binding.itemRecView.adapter = customersAdapter
        binding.labelTv.text = getString(R.string.companies)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.customers.collectLatest {
                    customersAdapter.submitList(it)
                }
            }
        }
    }

    private fun addEditTextListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchCustomers(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}
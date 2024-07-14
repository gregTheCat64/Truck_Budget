package ru.javacat.ui.edit_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.common.utils.formatCompanyName
import ru.javacat.common.utils.toShortCompanyName
import ru.javacat.domain.models.Company
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

        binding.saveBtn.setOnClickListener {
            val newCompanyName = binding.searchEditText.text.toString().trim()
            val newCompany = if (newCompanyName.isNotEmpty()) {
                Company(0,
                    nameToShow = newCompanyName.formatCompanyName(),
                    shortName = newCompanyName.toShortCompanyName()
                    )
            } else null
            if (newCompany != null) {
                viewModel.insertNewCompany(newCompany)
                this.dismiss()
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }


    }

    private fun addEditTextListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchCustomers(p0.toString())
                binding.saveBtn.isVisible = !p0.isNullOrEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}
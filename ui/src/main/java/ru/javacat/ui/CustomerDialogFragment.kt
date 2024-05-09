package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Customer
import ru.javacat.ui.adapters.CustomersAdapter
import ru.javacat.ui.adapters.OnCustomerListener
import ru.javacat.ui.databinding.FragmentCustomerDialogBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.CustomersDialogViewModel

@AndroidEntryPoint
class CustomerDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentCustomerDialogBinding

    private lateinit var customersAdapter: CustomersAdapter

    private val viewModel: CustomersDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllCustomers()

        customersAdapter = CustomersAdapter(object : OnCustomerListener{
            override fun onCustomer(item: Customer) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.FILTER_CUSTOMER, item.id)
                bundle.putString(FragConstants.CUSTOMER_NAME, item.shortName)
                setFragmentResult(FragConstants.FILTER_ORDER, bundle)
                this@CustomerDialogFragment.dismiss()
            }
        })

        binding.customersRecView.adapter = customersAdapter

        binding.allCustomersBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(FragConstants.FILTER_CUSTOMER, 0L)
            setFragmentResult(FragConstants.FILTER_ORDER, bundle)
            this@CustomerDialogFragment.dismiss()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.customers.collectLatest {
                    customersAdapter.submitList(it)
                }
            }
        }

        binding.customerEditText.addTextChangedListener(object : TextWatcher {
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
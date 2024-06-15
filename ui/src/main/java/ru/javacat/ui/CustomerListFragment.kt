package ru.javacat.ui

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
import ru.javacat.domain.models.Company
import ru.javacat.ui.adapters.CustomersAdapter
import ru.javacat.ui.adapters.OnCustomerListener
import ru.javacat.ui.databinding.FragmentCustomerListBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.CustomerListViewModel

@AndroidEntryPoint
class CustomerListFragment: BaseFragment<FragmentCustomerListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCustomerListBinding
        get() = {inflater, container->
            FragmentCustomerListBinding.inflate(inflater, container, false)
        }
    private val  viewModel: CustomerListViewModel by viewModels()
    private lateinit var customersAdapter: CustomersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Заказчики"

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllCustomers()
        val bundle = Bundle()

        customersAdapter = CustomersAdapter(object : OnCustomerListener{
            override fun onCustomer(item: Company) {
                bundle.putLong(FragConstants.CUSTOMER_ID, item.id)
                findNavController().navigate(R.id.customerFragment, bundle)
            }
        })
        binding.customersRecView.adapter = customersAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.customers.collectLatest {
                    customersAdapter.submitList(it)
                }
            }
        }

        binding.addCustomerBtn.setOnClickListener {
            findNavController().navigate(R.id.newCustomerFragment)
        }
    }
}
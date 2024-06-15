package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.ChooseCustomerAdapter
import ru.javacat.ui.adapters.ChooseManagerAdapter
import ru.javacat.ui.databinding.FragmentAddCustomerBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import ru.javacat.ui.view_models.AddCustomerViewModel

@AndroidEntryPoint
class AddCustomerFragment : BaseFragment<FragmentAddCustomerBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddCustomerBinding
        get() = { inflater, container ->
            FragmentAddCustomerBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddCustomerViewModel by viewModels()
    private lateinit var customerAdapter: ChooseCustomerAdapter
    private lateinit var employeeAdapter: ChooseManagerAdapter

    private var isNewOrder = true

    private var customerId = 0L
    private var routeId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER) ?: true
        Log.i("AddCustomFrag", "isNewOrder: $isNewOrder")
        routeId = args?.getLong(FragConstants.ROUTE_ID)?:0L
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity). supportActionBar?.title = "Клиент"

        initUi()
        initCustomerAdapter()
        initEmployeeAdapter()
        addCustomerEditTextListener()
        addManagerEditTextListener()
        loadStateListener()

        binding.cancelBtn.setOnClickListener {
            if (isNewOrder){
                findNavController().popBackStack(R.id.viewPagerFragment, false)
            } else findNavController().popBackStack(R.id.orderDetailsFragment, false)
        }

        binding.newCustomerBtn.setOnClickListener {
            findNavController().navigate(R.id.newCustomerFragment)
        }

        binding.newManagerBtn.setOnClickListener {
            val bundle = Bundle()
            if (customerId != 0L){
                bundle.putLong(FragConstants.CUSTOMER_ID, customerId)
                findNavController().navigate(R.id.newEmployeeFragment, bundle)
            }
        }

        binding.okBtn.setOnClickListener {
            viewModel.addCustomerToOrder(routeId)
        }
    }

    private fun initUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    order.customer?.let {
                        binding.customerInputEditText.setText(it.nameToShow)
                    }
                    order.manager?.let {
                        binding.managerInputEditText.setText(it.nameToShow)
                    }
                }
            }
        }
    }

    private fun initCustomerAdapter() {
        viewModel.getCustomers()
        customerAdapter = ChooseCustomerAdapter {
            viewModel.setCustomer(it)
            binding.customerInputEditText.setText(it.nameToShow)
            customerId = it.id
            viewModel.getEmployee(it.id)
            binding.managerLayout.isGone = false
        }
        binding.customersRecView.adapter = customerAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.customers.collectLatest {
                    customerAdapter.submitList(it)
                }
            }
        }
    }

    private fun initEmployeeAdapter() {
        employeeAdapter = ChooseManagerAdapter {
            viewModel.setEmployee(it)
            binding.managerInputEditText.setText(it.nameToShow)
        }
        binding.employeesRecView.adapter = employeeAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.managers.collectLatest {
                    employeeAdapter.submitList(it)
                }
            }
        }
    }

    private fun addCustomerEditTextListener() {
        binding.customerInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchCustomers(p0.toString())
                binding.managerLayout.isGone = p0.isNullOrEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun addManagerEditTextListener() {
        binding.managerInputEditText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchEmployee(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun loadStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadState.collectLatest {
                    if (it is LoadState.Success.OK) {
                        if (isNewOrder) {
                            findNavController().navigate(R.id.addCargoFragment)
                        } else findNavController().popBackStack(R.id.orderDetailsFragment, false)
                    }
                    if (it is LoadState.Error) {
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.fill_requested_fields),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.ChooseCustomerAdapter
import ru.javacat.ui.databinding.FragmentAddCustomerBinding
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding
import ru.javacat.ui.view_models.AddCustomerViewModel

@AndroidEntryPoint
class AddCustomerFragment : BaseFragment<FragmentAddCustomerBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddCustomerBinding
        get() = { inflater, container ->
            FragmentAddCustomerBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddCustomerViewModel by viewModels()
    private lateinit var adapter: ChooseCustomerAdapter

    private var isNewOrder = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER) ?: true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        initAdapter()
        addEditTextListener()
        loadStateListener()

        binding.cancelBtn.setOnClickListener {
            if (isNewOrder){
                findNavController().navigate(R.id.routeFragment)
            } else findNavController().navigate(R.id.orderDetailsFragment)
        }

    }

    private fun initUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    order.customer?.let {
                        binding.customerInputEditText.setText(it.name)
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        viewModel.getCustomers()
        adapter = ChooseCustomerAdapter {
            viewModel.addCustomerToOrder(it)
            //findNavController().navigate()
        }
        binding.customersRecView.adapter = adapter

        binding.newCustomerBtn.setOnClickListener {
            findNavController().navigate(R.id.newCustomerFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.customers.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun addEditTextListener() {
        binding.customerInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchCustomers(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun loadStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadState.collectLatest {
                    if (it is LoadState.Success.GoForward) {
                        if (isNewOrder) {
                            findNavController().navigate(R.id.addCargoFragment)
                        } else findNavController().navigateUp()
                    }
                }
            }
        }
    }
}
package ru.javacat.ui.new_route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Order
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ChooseCustomerAdapter
import ru.javacat.ui.databinding.FragmentAddCustomerBinding
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import ru.javacat.ui.view_models.AddCustomerViewModel

@AndroidEntryPoint
class AddCustomerDialog: DialogFragment() {
    private val viewModel: AddCustomerViewModel by viewModels()
    private lateinit var adapter: ChooseCustomerAdapter
    private lateinit var binding: FragmentAddCustomerBinding
    private var isNewOrder = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER) ?: true

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddCustomerBinding.inflate(layoutInflater)

        return binding.root
    }

//        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//
//
//        val listener = DialogInterface.OnClickListener{_,i->
//            when (i) {
//                DialogInterface.BUTTON_NEGATIVE -> this.dismiss()
//                DialogInterface.BUTTON_POSITIVE -> {
//
//                }
//            }
//        }
//
//        val builder = AlertDialog.Builder(requireContext())
//            .setPositiveButton("Save", listener)
//            .setNegativeButton("Cancel", listener)
//            .create()
//
//        return builder
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedOrder.collectLatest { order->
                    if (order != null) {
                        init(order)
                    }
                }
            }
        }

        initAdapter()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun init(order: Order){
        order.customer.let {
            binding.customerInputEditText.setText(it?.nameToShow)
        }
    }

    private fun initAdapter() {
        viewModel.getCustomers()
        adapter = ChooseCustomerAdapter {
            viewModel.setCustomer(it)
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
}
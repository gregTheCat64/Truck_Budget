package ru.javacat.ui.edit_order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.adapters.ChooseCompanyAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding

@AndroidEntryPoint
class EditCustomerDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemBinding
    private lateinit var customersAdapter: ChooseCompanyAdapter
    private val viewModel: EditCustomerDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseItemBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getCustomers()
        customersAdapter = ChooseCompanyAdapter {
            viewModel.addCustomerToOrder(it)
            this.dismiss()
        }
        binding.itemList.adapter = customersAdapter
        binding.itemNameTextView.text = "Компании"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.customers.collectLatest {
                    customersAdapter.submitList(it)
                }
            }
        }
    }
}
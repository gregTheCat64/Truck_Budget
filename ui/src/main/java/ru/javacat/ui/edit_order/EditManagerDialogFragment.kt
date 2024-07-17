package ru.javacat.ui.edit_order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ChooseManagerChipAdapter
import ru.javacat.ui.adapters.my_adapter.ChooseManagerAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class EditManagerDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemBinding
    private lateinit var managerAdapter: ChooseManagerAdapter
    private var customerId: Long? = null
    private val viewModel: EditManagerDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseItemBinding.inflate(layoutInflater)
        customerId = arguments?.getLong(FragConstants.COMPANY_ID)
        Log.i("EditManagerDialogFragment", "customerId: $customerId")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId?.let { viewModel.getEmployee(it) }

        managerAdapter = ChooseManagerAdapter {
            viewModel.addManagerToOrder(it)
            this.dismiss()
        }

        binding.itemList.adapter = managerAdapter
        binding.itemNameTextView.text = getString(R.string.employee)
        binding.newItemBtn.text = getString(R.string.create_new_employee)
        binding.newItemBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)
            customerId?.let { bundle.putLong(FragConstants.COMPANY_ID, it) }
            findNavController().navigate(R.id.newEmployeeFragment, bundle)
            this.dismiss()

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.managers.collectLatest {
                    managerAdapter.submitList(it)
                }
            }
        }
    }
}
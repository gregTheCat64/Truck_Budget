package ru.javacat.ui.new_route

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
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ChooseDriverAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class ChooseTruckDriverFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseItemBinding
    private val viewModel: ChooseTruckDriverViewModel by viewModels()
    private lateinit var driversAdapter: ChooseDriverAdapter

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

        val args = arguments

        val companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L

        initDriversCase(companyId)
    }

    private fun initDriversCase(companyId: Long){
        binding.newItemBtn.text = getString(R.string.create_new_truck_driver)
        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            val bundle = Bundle()
            bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)
            bundle.putLong(FragConstants.COMPANY_ID, companyId)
            findNavController().navigate(R.id.newDriverFragment, bundle)
        }

        viewModel.getDriver(companyId)
        driversAdapter = ChooseDriverAdapter {
            viewModel.setDriver(it)
            this.dismiss()
        }
        binding.itemList.adapter = driversAdapter

        binding.itemNameTextView.text = "Водители"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.drivers.collectLatest {
                    driversAdapter.submitList(it)

                }
            }
        }
    }

}
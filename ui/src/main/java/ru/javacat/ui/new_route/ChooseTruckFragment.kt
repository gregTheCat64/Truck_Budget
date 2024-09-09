package ru.javacat.ui.new_route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
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
import ru.javacat.ui.adapters.ChooseTruckAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class ChooseTruckFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseItemBinding

    private val viewModel: NewRouteViewModel by activityViewModels()
    private lateinit var trucksAdapter: ChooseTruckAdapter

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

        initTrucksCase(companyId)
    }

    private fun initTrucksCase(companyId: Long){
        val bundle = Bundle()
        bundle.putLong(FragConstants.COMPANY_ID, companyId)
        bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)

        binding.newItemBtn.setText(getString(R.string.create_new_truck))

        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            findNavController().navigate(R.id.newTruckFragment, bundle)
        }

        viewModel.getTrucks(companyId)
        trucksAdapter = ChooseTruckAdapter {
            Log.i("ChoseItemFrag", "changing Truck")
            viewModel.setTruck(it)
            this.dismiss()
            //findNavController().navigateUp()
        }
        binding.itemList.adapter = trucksAdapter

        binding.itemNameTextView.text = "Тягачи"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trucks.collectLatest {
                    trucksAdapter.submitList(it)
                }
            }
        }
    }
}
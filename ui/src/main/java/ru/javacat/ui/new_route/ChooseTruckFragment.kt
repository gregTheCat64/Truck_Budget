package ru.javacat.ui.new_route

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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
import ru.javacat.domain.models.Truck
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ChooseTruckAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class ChooseTruckFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseItemWithSearchBinding

    private val viewModel: NewRouteViewModel by activityViewModels()
    private lateinit var trucksAdapter: ChooseTruckAdapter

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

        val args = arguments

        val companyId = args?.getLong(FragConstants.COMPANY_ID)?:-1L

        initTrucksCase(companyId)
        addEditTextListener()

        binding.saveBtn.setOnClickListener {
            val truckName = binding.searchEditText.text.toString().trim()
            if (truckName.isNotEmpty()){
                viewModel.saveNewTruck(Truck(
                    id = 0L,
                    companyId = companyId,
                    regNumber = truckName
                ))
                this.dismiss()
            }
        }
    }

    private fun initTrucksCase(companyId: Long){
        val bundle = Bundle()
        bundle.putLong(FragConstants.COMPANY_ID, companyId)
        bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)

        viewModel.getTrucks(companyId)
        trucksAdapter = ChooseTruckAdapter {
            Log.i("ChoseItemFrag", "changing Truck")
            viewModel.setTruck(it)
            this.dismiss()
        }
        binding.itemRecView.adapter = trucksAdapter
        binding.labelTv.text = "Тягачи"
        binding.searchEditText.hint = "Введите рег.номер без региона"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trucks.collectLatest {
                    trucksAdapter.submitList(it)
                }
            }
        }
    }

    private fun addEditTextListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //viewModel.sear(p0.toString())
                binding.saveBtn.isVisible = !p0.isNullOrEmpty()
                binding.saveBtn.text = "Создать тягач: $p0"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
}
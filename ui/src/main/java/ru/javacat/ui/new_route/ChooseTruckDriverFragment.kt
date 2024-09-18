package ru.javacat.ui.new_route

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.TruckDriver
import ru.javacat.ui.adapters.ChooseDriverAdapter
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class ChooseTruckDriverFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseItemWithSearchBinding
    private val viewModel: NewRouteViewModel by activityViewModels()
    private lateinit var driversAdapter: ChooseDriverAdapter

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

        initDriversCase(companyId)
        addEditTextListener()

        binding.saveBtn.setOnClickListener {
            val tdName = binding.searchEditText.text.toString().trim()
            if (tdName.isNotEmpty()){
                viewModel.saveNewTruckDriver(
                    TruckDriver(
                    id = 0L,
                    companyId = companyId,
                    surname = tdName
                )
                )
                this.dismiss()
            }
        }
    }

    private fun initDriversCase(companyId: Long){
        viewModel.getDriver(companyId)
        driversAdapter = ChooseDriverAdapter {
            viewModel.setDriver(it)
            this.dismiss()
        }
        binding.itemRecView.adapter = driversAdapter
        binding.labelTv.text = "Водители"
        binding.searchEditText.hint = "Введите фамилию нового водителя"

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.drivers.collectLatest {
                    driversAdapter.submitList(it)

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
                binding.saveBtn.text = "Создать водителя: $p0"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

}
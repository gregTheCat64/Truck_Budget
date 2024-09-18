package ru.javacat.ui.edit_order

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Manager
import ru.javacat.ui.R
import ru.javacat.ui.adapters.my_adapter.ChooseManagerAdapter
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class EditManagerDialogFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemWithSearchBinding
    private lateinit var managerAdapter: ChooseManagerAdapter
    private var customerId: Long? = null
    private val viewModel: EditManagerDialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseItemWithSearchBinding.inflate(layoutInflater)
        customerId = arguments?.getLong(FragConstants.COMPANY_ID)
        Log.i("EditManagerDialogFragment", "customerId: $customerId")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId?.let { viewModel.getEmployee(it) }
        addEditTextListener()

        managerAdapter = ChooseManagerAdapter {
            viewModel.addManagerToOrder(it)
            this.dismiss()
        }

        binding.itemRecView.adapter = managerAdapter
        binding.labelTv.text = getString(R.string.employee)
        binding.searchEditText.hint = "Введите имя сотрудника"


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.managers.collectLatest {
                    managerAdapter.submitList(it)
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            val newEmployeeName = binding.searchEditText.text.toString().trim()
            val newEmployee = if (newEmployeeName.isNotEmpty()) {
                Manager(
                    companyId = customerId?:0,
                    id = 0L,
                    firstName = newEmployeeName
                )
            } else null
            if (newEmployee != null) {
                viewModel.saveNewManager(newEmployee, true)
                this.dismiss()
            } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addEditTextListener() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchEmployee(p0.toString())
                binding.saveBtn.isVisible = !p0.isNullOrEmpty()
                binding.saveBtn.text = "Создать менеджера: $p0"
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }
}
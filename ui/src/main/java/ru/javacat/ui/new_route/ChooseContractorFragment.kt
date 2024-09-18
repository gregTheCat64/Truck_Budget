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
import ru.javacat.common.utils.toShortCompanyName
import ru.javacat.domain.models.Company
import ru.javacat.ui.adapters.ChooseCompanyAdapter
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding

@AndroidEntryPoint
class ChooseContractorFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemWithSearchBinding
    private val viewModel: NewRouteViewModel by activityViewModels()
    private lateinit var companiesAdapter: ChooseCompanyAdapter


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

        initContractorsCase()
        addEditTextListener()

        binding.saveBtn.setOnClickListener {
            val newCompanyName = binding.searchEditText.text.toString().trim()
            if (newCompanyName.isNotEmpty()){
                viewModel.saveNewContractor(Company(0,
                    nameToShow = newCompanyName,
                    shortName = newCompanyName.toShortCompanyName()
                    ))
                this.dismiss()
            }
        }
    }

    private fun initContractorsCase(){
        binding.labelTv.text = "Компании"
        binding.searchEditText.hint = "Введите название фирмы"

        viewModel.getContractors()
        companiesAdapter = ChooseCompanyAdapter {
            viewModel.setCompany(it)
            this.dismiss()
        }

        binding.itemRecView.adapter = companiesAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.contractors.collectLatest {
                    companiesAdapter.submitList(it)
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
                binding.saveBtn.text = "Создать фирму: $p0"
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

}
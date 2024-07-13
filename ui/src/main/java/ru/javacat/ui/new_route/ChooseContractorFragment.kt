package ru.javacat.ui.new_route

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ru.javacat.ui.adapters.ChooseCompanyAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class ChooseContractorFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentChooseItemBinding
    private val viewModel: ChooseContractorViewModel by viewModels()
    private lateinit var companiesAdapter: ChooseCompanyAdapter


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
        initContractorsCase()

    }

    private fun initContractorsCase(){
        binding.itemNameTextView.text = "Компании"
        binding.newItemBtn.text = getString(R.string.create_new_partner)
        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            val bundle = Bundle()
            bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)
            findNavController().navigate(R.id.newCustomerFragment, bundle)
        }
        viewModel.getContractors()
        companiesAdapter = ChooseCompanyAdapter {
            viewModel.setCompany(it)
            this.dismiss()
        }
        binding.itemList.adapter = companiesAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.contractors.collectLatest {
                    companiesAdapter.submitList(it)
                }
            }
        }
    }

}
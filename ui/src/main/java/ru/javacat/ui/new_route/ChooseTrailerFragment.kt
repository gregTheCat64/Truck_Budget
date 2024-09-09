package ru.javacat.ui.new_route

import android.os.Bundle
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
import ru.javacat.ui.adapters.ChooseTrailerAdapter
import ru.javacat.ui.adapters.TrailersAdapter
import ru.javacat.ui.databinding.FragmentChooseItemBinding
import ru.javacat.ui.databinding.FragmentChooseItemWithSearchBinding
import ru.javacat.ui.utils.FragConstants


@AndroidEntryPoint
class ChooseTrailerFragment: BottomSheetDialogFragment() {
    private lateinit var binding: FragmentChooseItemBinding

    private val viewModel: NewRouteViewModel by activityViewModels()
    private lateinit var trailersAdapter: ChooseTrailerAdapter

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

        initTrailersCase(companyId)
    }

    private fun initTrailersCase(companyId: Long){
        val bundle = Bundle()
        bundle.putLong(FragConstants.COMPANY_ID, companyId)
        bundle.putBoolean(FragConstants.IS_NEED_TO_SET, true)

        binding.newItemBtn.setText(getString(R.string.create_new_trailer))

        binding.newItemBtn.setOnClickListener {
            this.dismiss()
            findNavController().navigate(R.id.newTrailerFragment, bundle)
        }

        viewModel.getTrailers(companyId)

        trailersAdapter = ChooseTrailerAdapter {
            viewModel.setTrailer(it)
            this.dismiss()
            //findNavController().navigateUp()
        }
        binding.itemList.adapter = trailersAdapter

        binding.itemNameTextView.text = "Прицепы"


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.trailers.collectLatest {
                    trailersAdapter.submitList(it)

                }
            }
        }
    }
}
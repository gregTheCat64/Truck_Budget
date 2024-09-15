package ru.javacat.ui.new_trailer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Trailer
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentTransportInfoBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class TrailerInfoFragment: BaseFragment<FragmentTransportInfoBinding>() {

    private val viewModel: TrailerInfoViewModel by viewModels()
    private var transportId: Long? = null
    private var companyId: Long = -1L

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentTransportInfoBinding
        get() = {inflater, container ->
            FragmentTransportInfoBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        companyId = args?.getLong(FragConstants.COMPANY_ID) ?: -1L
        transportId = args?.getLong(FragConstants.TRANSPORT_ID) ?: 0

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_edit_remove, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    R.id.edit_menu_item -> {
                        editTrailer()
                        return true
                    }

                    R.id.remove_menu_item -> {
                        transportId?.let { hideTrailer(it) }
                        return true
                    }

                    else -> return false
                }
            }
        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (transportId!=0L){
            transportId?.let { viewModel.getTrailer(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trailer.collectLatest {
                    if (it != null) {
                        updateUi(it)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    when (it) {
                        is LoadState.Loading -> {
                            binding.progressBar.isGone = false
                        }
                        is LoadState.Success.Removed -> {
                            findNavController().navigateUp()
                            Toast.makeText(requireContext(), getString(R.string.removed), Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun updateUi(tr: Trailer){
        (activity as AppCompatActivity).supportActionBar?.title = tr.nameToShow

        val plate = "${tr.regNumber} ${tr.regionCode}"

        binding.apply {
            regNumberValue.text = plate
            vinValue.text = tr.vin
            modelValue.text = tr.model
            typeOfTransportValue.text = tr.type
            yearOfManufacturingValue.text = tr.yearOfManufacturing
        }
    }

    private fun editTrailer(){
        if (transportId!=null){
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, companyId)
            bundle.putLong(FragConstants.TRANSPORT_ID, transportId!!)
            findNavController().navigate(R.id.newTrailerFragment, bundle)
        }else Toast.makeText(requireContext(), "Something wrong, td id: $transportId", Toast.LENGTH_SHORT).show()

    }

    private fun hideTrailer(id: Long){
        viewModel.hideTrailer(id)
    }
}
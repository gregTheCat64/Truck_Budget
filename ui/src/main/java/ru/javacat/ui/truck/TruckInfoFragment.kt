package ru.javacat.ui.truck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
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
import ru.javacat.domain.models.Truck
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.LoadState
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentTransportInfoBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.shareMessage

@AndroidEntryPoint
class TruckInfoFragment: BaseFragment<FragmentTransportInfoBinding>() {

    private val viewModel: TruckInfoViewModel by viewModels()
    private var transportId: Long? = null
    private var companyId: Long = -1L
    private var currentTruck: Truck? = null

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
        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

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
                        editTruck()
                        return true
                    }

                    R.id.remove_menu_item -> {
                        transportId?.let { hideTruck(it) }
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
            transportId?.let { viewModel.getTruck(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedTruck.collectLatest {
                    if (it != null) {
                        currentTruck = it
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

        binding.actionBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.actionBar.editBtn.setOnClickListener {
            editTruck()
        }

        binding.actionBar.moreBtn.setOnClickListener {
            showMenu(it)
        }
    }

    private fun updateUi(tr: Truck){
        //(activity as AppCompatActivity).supportActionBar?.title = tr.nameToShow
        binding.actionBar.title.text = tr.nameToShow
        val plate = "${tr.regNumber} ${tr.regionCode}"

        binding.apply {
            regNumberValue.text = plate
            vinValue.text = tr.vin
            modelValue.text = tr.model
            typeOfTransportValue.text = tr.type
            yearOfManufacturingValue.text = tr.yearOfManufacturing
        }
    }

    private fun editTruck(){
        if (transportId!=null){
            val bundle = Bundle()
            bundle.putLong(FragConstants.COMPANY_ID, companyId)
            bundle.putLong(FragConstants.TRANSPORT_ID, transportId!!)
            findNavController().navigate(R.id.newTruckFragment, bundle)
        }else Toast.makeText(requireContext(), "Something wrong, td id: $transportId", Toast.LENGTH_SHORT).show()
    }

    private fun hideTruck(id: Long){
        viewModel.hideTruck(id)
    }

    private fun share(tr: Truck){
        val plateInfo = if (tr.regNumber.isNotEmpty()) "Рег. номер: ${tr.regNumber} ${tr.regionCode.takeIf { it != null }}" else null
        val typeInfo = if (tr.type.isNotEmpty()) tr.type else null
        val modelInfo = if (!tr.model.isNullOrEmpty()) tr.model else null
        val year = if (!tr.yearOfManufacturing.isNullOrEmpty()) tr.yearOfManufacturing else null

        val infoToShare = listOfNotNull(plateInfo, typeInfo, modelInfo, year).joinToString(", ")

        requireContext().shareMessage(infoToShare)
    }

    private fun showMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.menu_remove)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item->
            when (item.itemId) {
                R.id.remove_menu_item -> {
                    transportId?.let { hideTruck(it) }
                    //findNavController().navigateUp()

                }
                R.id.share_menu_item -> {
                    currentTruck?.let { share(it) }
                }

                else -> Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            true
        })
        menu.show()
    }
}
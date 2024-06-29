package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import ru.javacat.domain.models.Cargo
import ru.javacat.domain.models.CargoName
import ru.javacat.domain.models.Order
import ru.javacat.ui.adapters.CargoAdapter
import ru.javacat.ui.databinding.FragmentAddCargoBinding
import ru.javacat.ui.utils.FragConstants.IS_NEW_ORDER
import ru.javacat.ui.view_models.AddCargoViewModel

@AndroidEntryPoint
class AddCargoFragment : BaseFragment<FragmentAddCargoBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddCargoBinding
        get() = { inflater, container ->
            FragmentAddCargoBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddCargoViewModel by viewModels()
    private lateinit var cargoAdapter: CargoAdapter
    private var cargosFound: Boolean = false
    private var cargoList:List<CargoName> = emptyList()

    private var weight: Int = 0
    private var volume: Int = 0
    private var name: String = ""
    private var isBackLoad = true
    private var isSideLoad = false
    private var isTopLoad = false

    private var isNewOrder = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        isNewOrder = args?.getBoolean(IS_NEW_ORDER) ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.show()

        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_cancel, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        return true
                    }
                    R. id.cancel_button_menu_item-> {
                        if (isNewOrder){
                            findNavController().popBackStack(R.id.viewPagerFragment, false)
                        } else findNavController().popBackStack(R.id.orderDetailsFragment, false)
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

        addEditTextListener()
        initAdapter()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editedOrder.collectLatest { order ->
                    if (order != null) {
                        initUi(order)
                    }
                }
            }
        }

        //Навигация
        loadStateListener()



        binding.okBtn.setOnClickListener {
            val cargoName = binding.cargoEditText.text.toString()
            if (cargoName.isEmpty()){
                Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkCargoInStorage(cargoName)){
                addCargoToStorage(cargoName)
            }
            getFields()
            val cargo = Cargo(weight,volume,name, isBackLoad, isSideLoad, isTopLoad)
            viewModel.addCargoToOrder(cargo)
        }
    }

    private fun checkCargoInStorage(cargoToCheck: String): Boolean{
        for (i in cargoList){
            if (i.nameToShow == cargoToCheck) return true
        }
        return false
    }

    private fun addCargoToStorage(cargoName: String){
        viewModel.insertNewCargo(CargoName(null, cargoName))
    }

    private fun initUi(order: Order) {
        order.cargo?.cargoName.let {
            if (!it.isNullOrEmpty()) binding.cargoEditText.setText(it)
        }
        order.cargo?.cargoVolume.let {
            if (it != null) binding.tvVolume.setText(it.toString())
        }
        order.cargo?.cargoWeight.let {
            if (it != null) binding.weightTv.setText(it.toString())
        }
    }

    private fun initAdapter() {
        viewModel.getCargos()

        cargoAdapter = CargoAdapter {
            binding.cargoEditText.setText(it.nameToShow)
            binding.cargoRecView.isGone = true
        }

        binding.cargoRecView.adapter = cargoAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cargo.collectLatest {
                    // Log.i("OrderFrag", "cargos: $it")
                    if (it != null) {
                        cargoList = it
                    }
                    cargoAdapter.submitList(it)
                    cargosFound = it?.size != 0
                    //binding.addNewCargoBtn.isGone = cargosFound
                }
            }
        }
    }

    private fun getFields() {
        binding.tvVolume.text.let {
            volume = if (it?.isNotEmpty() == true) it.toString().toInt() else 82
        }
        binding.weightTv.text.let {
            weight = if (it?.isNotEmpty() == true) it.toString().toInt() else 20
        }
        binding.cargoEditText.text.let {
            name = if (it?.isNotEmpty() == true) it.toString() else ""
        }
        isBackLoad = binding.backCheck.isChecked

        isSideLoad = binding.sideCheck.isChecked

        isTopLoad = binding.upCheck.isChecked
    }

    private fun addEditTextListener() {
        binding.cargoEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.cargoRecView.isGone = false
                viewModel.searchCargos(p0.toString())

//                if (!cargosFound && p0?.length != 0) {
//                    binding.addNewCargoBtn.isVisible = true
//                    binding.addNewCargoBtn.text = "Сохранить $p0"
//                } else binding.addNewCargoBtn.isGone = true
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun loadStateListener() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadState.collectLatest {
                    if (it is LoadState.Success.GoForward) {
                        if (isNewOrder) {
                            findNavController().navigate(R.id.addPointsFragment)
                        } else findNavController().navigateUp()

                    }
                }
            }
        }
    }
}
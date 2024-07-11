package ru.javacat.ui.new_route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.ChooseItemFragment
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentCreateRouteBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.NewRouteViewModel

    const val itemParam = "item"
@AndroidEntryPoint
class NewRouteFragment: BaseFragment<FragmentCreateRouteBinding>() {

    private val viewModel: NewRouteViewModel by viewModels()
    private val bundle = Bundle()
    private var isLastRouteLoaded = false
    private var companyId: Long? = null

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCreateRouteBinding
        get() = {inflater, container ->
            FragmentCreateRouteBinding.inflate(inflater, container, false)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)

        requireActivity().addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_empty, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
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

        //TODO добавить тут инициацию по первому входу во фрагмент

        //Получаем прошлый рейс и выставляем последние значения
        if (!isLastRouteLoaded){
            setLastRouteToCurrent()
            isLastRouteLoaded = true
        }


        binding.addContractorEditText.setOnClickListener {
            addItemToRoute("CONTRACTOR")
        }

        //Добавляем водителя
        binding.addDriverEditText.setOnClickListener {
            addItemToRoute("DRIVER")
        }

        //Добавляем тягач
        binding.addTruckEditText.setOnClickListener {
            addItemToRoute("TRUCK")
        }

        //Добавляем прицеп
        binding.addTrailerEditText.setOnClickListener {
            addItemToRoute("TRAILER")
        }

        //Обновляем и сохраняем
        binding.nextBtn.setOnClickListener {
            saveRoute()
        }

        //Инициализация ui
        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.chosenCompany.collectLatest {
                    binding.addContractorEditText.setText(it?.nameToShow)
                    companyId = it?.id?:0L
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.chosenDriver.collectLatest {
                    binding.addDriverEditText.setText(it?.surname)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.chosenTruck.collectLatest {
                    binding.addTruckEditText.setText(it?.regNumber)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.chosenTrailer.collectLatest {
                    binding.addTrailerEditText.setText(it?.regNumber)
                }
            }
        }


        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.routeId.collectLatest {
                    Log.i("NewRouteFrag", "routeID: $it")
                    val bundle = Bundle()
                    if (it != null) {
                        bundle.putLong(FragConstants.ROUTE_ID, it)
                        findNavController().navigate(R.id.routeViewPagerFragment, bundle)
                    }
                }
            }
        }

    }

    private fun setLastRouteToCurrent(){
        viewModel.setLastRouteToEditedRoute()
    }

    private fun addItemToRoute(item: String){
        bundle.putLong(FragConstants.COMPANY_ID, companyId?:0L)
        bundle.putString(itemParam, item)
        val dialogFragment = ChooseItemFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "")
        //findNavController().navigate(R.id.chooseItemFragment, bundle)
    }


    private fun saveRoute(){
        binding.prepayEditText.let {
            if (it.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
                return
            } else {
                val prepay = it.text.toString().toInt()
                viewModel.setRouteParameters(prepay)
                viewModel.saveNewRoute()
            }
        }
    }

}
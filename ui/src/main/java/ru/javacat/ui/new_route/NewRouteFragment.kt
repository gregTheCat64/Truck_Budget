package ru.javacat.ui.new_route

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Route
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentCreateRouteBinding
import ru.javacat.ui.utils.FragConstants

const val itemParam = "item"
@AndroidEntryPoint
class NewRouteFragment: BaseFragment<FragmentCreateRouteBinding>() {

    private val viewModel: NewRouteViewModel by activityViewModels()
    private val bundle = Bundle()
    private var isLastRouteLoaded = false
    private var companyId: Long? = null
    private var currentRoute: Route? = null
    private var routeId: Long? = null

    private val TAG = "NewRouteFrag"

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCreateRouteBinding
        get() = {inflater, container ->
            FragmentCreateRouteBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        routeId = arguments?.getLong(FragConstants.ROUTE_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)

//        requireActivity().addMenuProvider(object : MenuProvider{
//            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
//                menuInflater.inflate(R.menu.menu_empty, menu)
//            }
//
//            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
//                when (menuItem.itemId) {
//                    android.R.id.home -> {
//                        findNavController().navigateUp()
//                        return true
//                    }
//                    else -> return false
//                }
//            }
//        }, viewLifecycleOwner)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("NewRouteFrag", "lastRouteLoaded: $isLastRouteLoaded")

        Log.i(TAG, "routeID: $routeId")

        if (routeId == null && !isLastRouteLoaded){
            setLastRouteToCurrent()
            isLastRouteLoaded = true
        } else restoreCurrentRoute(routeId!!)

        binding.actionBar.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.actionBar.saveBtn.setOnClickListener {
            currentRoute?.let { it1 -> saveRoute(it1) }
        }

        binding.addContractorEditText.setOnClickListener {
            addContractorToRoute()
        }

        //Добавляем водителя
        binding.addDriverEditText.setOnClickListener {
            addTruckDriverToRoute()
        }

        //Добавляем тягач
        binding.addTruckEditText.setOnClickListener {
            addTruckToRoute()
        }

        //Добавляем прицеп
        binding.addTrailerEditText.setOnClickListener {
            addTrailerToRoute()
        }

        //Обновляем и сохраняем
        binding.nextBtn.setOnClickListener {
            currentRoute?.let { it1 -> saveRoute(it1) }
        }

        //Инициализация ui
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.currentRoute.collectLatest {
                    Log.i("NewRouteFrag", "collecting newRoute: $it")
                    companyId = it?.contractor?.company?.id
                    currentRoute = it
                    it?.let { initUi(it) }
                }
            }
        }


        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.routeId.collectLatest {
                    Log.i(TAG, "newRouteID: $it")
                    if (it != null) {
                        //bundle.putLong(FragConstants.ROUTE_ID, it)
                        viewModel.clearRouteId()
                        findNavController().navigate(R.id.navigation_route_list)
                    }
                }
            }
        }
    }

    private fun initUi(route: Route){
        binding.actionBar.title.text = getString(R.string.edit)
        route.contractor?.company?.let {
            binding.companyNameAdvise.isGone = !it.nameToShow.contains("Моя Компания", true)
            binding.addContractorEditText.setText(it.nameToShow)
        }

        route.contractor?.driver?.let { td ->
            //val driverName = "${td.surname} ${td.firstName.takeIf { it!=null }}"
                binding.addDriverEditText.setText(td.nameToShow)
        }?:binding.addDriverEditText.text?.clear()

        route.contractor?.truck?.let {
            binding.addTruckEditText.setText(it.nameToShow)
        }?:binding.addTruckEditText.text?.clear()

        route.contractor?.trailer?.let {
            binding.addTrailerEditText.setText(it.regNumber)
        }?:binding.addTrailerEditText.text?.clear()

        route.prepayment.let {
            if (it != 0) binding.prepayEditText.setText(it.toString()) else binding.prepayEditText.text?.clear()
        }
    }

    private fun setLastRouteToCurrent(){
        Log.i(TAG, "settingLastRoute")
        viewModel.setLastRouteToEditedRoute()
    }

    private fun restoreCurrentRoute(id: Long){
        viewModel.restoreCurrentRoute(id)
    }

    private fun addContractorToRoute(){
        bundle.putLong(FragConstants.COMPANY_ID, companyId?:0L)
        val dialogFragment = ChooseContractorFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "")
        //findNavController().navigate(R.id.chooseItemFragment, bundle)
    }

    private fun addTruckToRoute(){
        bundle.putLong(FragConstants.COMPANY_ID, companyId?:0L)
        val dialogFragment = ChooseTruckFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "")
    }

    private fun addTrailerToRoute(){
        bundle.putLong(FragConstants.COMPANY_ID, companyId?:0L)
        val dialogFragment = ChooseTrailerFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "")
    }

    private fun addTruckDriverToRoute(){
        bundle.putLong(FragConstants.COMPANY_ID, companyId?:0L)
        val dialogFragment = ChooseTruckDriverFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "")
    }


    private fun saveRoute(route: Route){
        if (route.contractor?.driver != null && route.contractor?.truck != null){
            val prepay = if (!binding.prepayEditText.text.isNullOrEmpty()){
                binding.prepayEditText.text?.toString()?.toInt()?:0
            } else 0
            viewModel.setPrepayment(prepay)
            viewModel.saveNewRoute(route)
        } else Toast.makeText(requireContext(), getString(R.string.fill_requested_fields), Toast.LENGTH_SHORT).show()
    }

}
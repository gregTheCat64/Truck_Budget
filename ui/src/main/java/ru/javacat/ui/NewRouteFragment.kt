package ru.javacat.ui

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
import ru.javacat.ui.databinding.FragmentCreateRouteBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.view_models.NewRouteViewModel

    const val itemParam = "item"
@AndroidEntryPoint
class NewRouteFragment: BaseFragment<FragmentCreateRouteBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: NewRouteViewModel by viewModels()
    private val bundle = Bundle()
    private var isLastRouteLoaded = false
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

        val newRouteTitle = getString(R.string.new_route)

        (activity as AppCompatActivity).supportActionBar?.title = newRouteTitle

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
                viewModel.editedRoute.collectLatest {
                    binding.addDriverEditText.setText(it?.driver?.surname)
                    binding.addTruckEditText.setText(it?.truck?.regNumber)
                    binding.addTrailerEditText.setText(it?.trailer?.regNumber)
                    binding.prepayEditText.setText(it.prepayment?.toString())
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
                        findNavController().navigate(R.id.viewPagerFragment, bundle)
                    }

                }
            }
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED){
//                viewModel.loadState.collectLatest {
//                    if (it is LoadState.Success.OK) {
//                        findNavController().navigate(R.id.viewPagerFragment)
//                    }
//                }
//            }
//        }
    }

    private fun setLastRouteToCurrent(){
        viewModel.setLastRouteToEditedRoute()
    }

    private fun addItemToRoute(item: String){
        bundle.putString(itemParam, item)
        val dialogFragment = ChooseItemFragment()
        dialogFragment.arguments = bundle
        dialogFragment.show(parentFragmentManager, "")
        //findNavController().navigate(R.id.chooseItemFragment, bundle)
    }

    private fun saveRoute(){
        binding.prepayEditText.let {
            if (it.text.isNullOrEmpty()){
                Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
                return
            } else {
                val prepay = it.text.toString().toInt()
                viewModel.setRouteParameters(prepay)
                viewModel.saveNewRoute()
            }
        }
    }

}
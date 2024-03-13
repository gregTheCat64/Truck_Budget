package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.databinding.FragmentCreateRouteBinding
import ru.javacat.ui.view_models.CreateRouteViewModel


    const val itemParam = "item"
@AndroidEntryPoint
class CreateRouteFragment: BaseFragment<FragmentCreateRouteBinding>() {

    private val viewModel: CreateRouteViewModel by viewModels()
    private val bundle = Bundle()
    private var isLastRouteLoaded = false
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCreateRouteBinding
        get() = {inflater, container ->
            FragmentCreateRouteBinding.inflate(inflater, container, false)
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

        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedRoute.collectLatest {
                    binding.addDriverEditText.setText(it.driver?.fullName)
                    binding.addTruckEditText.setText(it.truck?.regNumber)
                    binding.addTrailerEditText.setText(it.trailer?.regNumber)
                    binding.prepayEditText.setText(it.prepayment?.toString())
                }
            }
        }

        //Навигация
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    if (it is LoadState.Success.OK) {
                        findNavController().navigate(R.id.routeFragment)
                    }
                }
            }
        }


    }

    private fun setLastRouteToCurrent(){
        viewModel.setLastRouteToEditedRoute()
    }

    private fun addItemToRoute(item: String){
        bundle.putString(itemParam, item)
        findNavController().navigate(R.id.chooseItemFragment, bundle)
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
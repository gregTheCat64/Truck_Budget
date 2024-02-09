package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.javacat.common.utils.toBase64
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.ui.adapters.OnPointListener
import ru.javacat.ui.adapters.PointsAdapter
import ru.javacat.ui.databinding.FragmentAddPointsBinding
import ru.javacat.ui.view_models.AddPointsViewModel
import java.time.LocalDate

@AndroidEntryPoint
class AddPointsFragment: BaseFragment<FragmentAddPointsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddPointsBinding
        get() = {inflater, container->
            FragmentAddPointsBinding.inflate(inflater,container,false)
        }


    private val viewModel: AddPointsViewModel by viewModels()
    private lateinit var adapter: PointsAdapter
    private lateinit var pointDate: LocalDate


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pointDate = LocalDate.now()

        adapter = PointsAdapter(object : OnPointListener{
            override fun removePoint(item: Point) {
                viewModel.removePoint(item)
            }
        })
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            viewModel.points.collect{
                Log.i("MyTag","it: %$it")
                adapter.submitList(it)
            }
        }


        binding.plusDayBtn.setOnClickListener {
            increaseDay()
        }

        binding.minusDayBtn.setOnClickListener {
            decreaseDay()
        }

        binding.addPointBtn.setOnClickListener {
            val place = binding.locationEditText.text.toString()
            val id = place.toBase64()
            println("id of $place: $id")
            //val newPoint = Point(id, Location(place), pointDate)
            //viewModel.addPoint(newPoint)
            binding.locationEditText.text?.clear()
            increaseDay()
        }
    }

    private fun increaseDay(){
        pointDate = pointDate.plusDays(1)
        binding.dateTextView.text = pointDate.toString()
    }

    private fun decreaseDay(){
        pointDate = pointDate.minusDays(1)
        binding.dateTextView.text = pointDate.toString()
    }
}
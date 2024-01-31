package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.domain.models.Location
import ru.javacat.domain.models.Point
import ru.javacat.ui.databinding.FragmentAddPointsBinding
import ru.javacat.ui.view_models.AddPointsViewModel
import java.time.LocalDate

@AndroidEntryPoint
class AddPointsFragment: BaseFragment<FragmentAddPointsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddPointsBinding
        get() = {inflater, container->
            FragmentAddPointsBinding.inflate(inflater,container,false)
        }

    val viewModel: AddPointsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var nowDate = LocalDate.now()

        val dateTextView = binding.dateTextView

        binding.dateTextView.setText((nowDate.plusDays(1)).toString())

        binding.plusDayBtn.setOnClickListener {
            nowDate= nowDate.plusDays(1)
            dateTextView.setText(nowDate.toString())
        }

        binding.minusDayBtn.setOnClickListener {
            nowDate= nowDate.minusDays(1)
            dateTextView.setText(nowDate.toString())
        }

        binding.addPointBtn.setOnClickListener {
            val place = binding.pointEditText.text.toString()
            val newPoint = Point(0, Location(place), nowDate)
            viewModel.addPoint(newPoint)
        }
    }
}
package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.databinding.FragmentAddPointsBinding
import java.time.LocalDate

@AndroidEntryPoint
class AddPointsFragment: BaseFragment<FragmentAddPointsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddPointsBinding
        get() = {inflater, container->
            FragmentAddPointsBinding.inflate(inflater,container,false)
        }

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
    }
}
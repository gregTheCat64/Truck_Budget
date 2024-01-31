package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.javacat.ui.databinding.FragmentOrderDetailsBinding

class OrderDetailsFragment:BaseFragment<FragmentOrderDetailsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentOrderDetailsBinding
        get() = {inflater, container ->
            FragmentOrderDetailsBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.routeTextView.setOnClickListener {
            findNavController().navigate(R.id.action_newOrderFragment_to_addPointsFragment)
        }
    }
}
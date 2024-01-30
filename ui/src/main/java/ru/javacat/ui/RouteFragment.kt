package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.javacat.ui.databinding.FragmentRouteBinding

class RouteFragment: BaseFragment<FragmentRouteBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteBinding
        get() = {inflater, container ->
            FragmentRouteBinding.inflate(inflater, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.newTruckBtn.setOnClickListener {
            findNavController().navigate(R.id.action_routeFragment_to_transportFragment)
        }

        binding.newTrailerBtn.setOnClickListener {
            findNavController().navigate(R.id.action_routeFragment_to_transportFragment)
        }

        binding.newDriverBtn.setOnClickListener {
            findNavController().navigate(R.id.action_routeFragment_to_newDriverFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }
}
package ru.javacat.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ru.javacat.ui.databinding.FragmentRouteListBinding

class RouteListFragment: BaseFragment<FragmentRouteListBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentRouteListBinding
        get() = {inflater, container->
            FragmentRouteListBinding.inflate(inflater, container, false)
        }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newRouteBtn.setOnClickListener {
            Log.i("MyTag", "clicked")
            findNavController().navigate(R.id.routeFragment)
        }

        binding.root.setOnClickListener {
            findNavController().navigate(R.id.orderListFragment)
        }
    }
}
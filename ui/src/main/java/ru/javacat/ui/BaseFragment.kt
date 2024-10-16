package ru.javacat.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding>: Fragment() {
    protected abstract val bindingInflater: (LayoutInflater, ViewGroup?) -> VB

    private var _binding: VB? = null

    protected open var bottomNavViewVisibility = View.VISIBLE

    protected val binding
        get() = _binding
            ?: throw NullPointerException("${this::class.simpleName} view binding failed")

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        if (activity is MainActivity) {
//            val mainActivity = activity as MainActivity
//            mainActivity.setBottomNavVisibility(bottomNavViewVisibility)
//        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("BaseFragment", "onResume")
//        if (activity is MainActivity) {
//            val mainActivity = activity as MainActivity
//            Log.i("BaseFragment", "bottomNavVis: $bottomNavViewVisibility")
//            mainActivity.setBottomNavVisibility(bottomNavViewVisibility)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i("BaseFragment", "onCreateView")
//        if (activity is MainActivity) {
//            val mainActivity = activity as MainActivity
//            mainActivity.setBottomNavVisibility(bottomNavViewVisibility)
//        }

        _binding = bindingInflater.invoke(inflater, container)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}
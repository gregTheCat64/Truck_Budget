package ru.javacat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.databinding.FragmentAddDocumentsBinding
import ru.javacat.ui.view_models.AddDocumentsViewModel

@AndroidEntryPoint
class AddDocumentsFragment: BaseFragment<FragmentAddDocumentsBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddDocumentsBinding
        get() = {inflater, container->
            FragmentAddDocumentsBinding.inflate(inflater, container, false)
        }

    private val viewModel: AddDocumentsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveBtn.setOnClickListener {
            val sendNumber = binding.sendingNumber.text.let {
                if (it.isNullOrEmpty()) null else it.toString()
            }
            if (sendNumber != null) {
                viewModel.addDocumentsToOrder(sendNumber)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.loadState.collectLatest {
                    if (it is LoadState.Success.OK) findNavController().navigate(R.id.orderDetailsFragment)
                }
            }
        }
    }
}
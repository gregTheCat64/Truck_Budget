package ru.javacat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import ru.javacat.ui.databinding.FragmentAddPaymentDetailsBinding
import ru.javacat.ui.databinding.FragmentNewTransportBinding


class AddPaymentDetailsFragment:BaseFragment<FragmentAddPaymentDetailsBinding>(){
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentAddPaymentDetailsBinding
        get() = {inflater, container->
            FragmentAddPaymentDetailsBinding.inflate(inflater, container, false)
        }
}
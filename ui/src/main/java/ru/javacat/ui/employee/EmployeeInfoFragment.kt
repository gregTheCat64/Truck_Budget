package ru.javacat.ui.employee

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Manager
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentEmployeeInfoBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.makePhoneCall
import ru.javacat.ui.utils.sendMessageToWhatsApp
import ru.javacat.ui.utils.shareMessage
import ru.javacat.ui.utils.showDeleteConfirmationDialog

@AndroidEntryPoint
class EmployeeInfoFragment: BaseFragment<FragmentEmployeeInfoBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentEmployeeInfoBinding
        get() = {inflater, container ->
            FragmentEmployeeInfoBinding.inflate(inflater, container, false)
        }

    private val viewModel: EmployeeInfoViewModel by viewModels()

    private var currentCompanyId: Long = 0L
    private var currentManagerId: Long? = null
    private var currentManager: Manager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        //currentCompanyId = args?.getLong(FragConstants.COMPANY_ID) ?: 0
        currentManagerId = args?.getLong(FragConstants.MANAGER_ID)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentManagerId?.let { viewModel.getManagerById(it) }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedManager.collectLatest {
                    if (it != null) {
                        currentCompanyId = it.companyId
                        currentManager = it
                        updateUi(it)
                    }
                }
            }
        }

        binding.actionBar.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.actionBar.editBtn.setOnClickListener {
            if (currentManagerId!= null){
                val bundle = Bundle()
                bundle.putLong(FragConstants.MANAGER_ID, currentManagerId!!)
                findNavController().navigate(R.id.action_employeeInfoFragment_to_newEmployeeFragment, bundle)
            }
        }

        binding.actionBar.moreBtn.setOnClickListener {
            showMenu(it)
        }

        binding.makeItMainNumberBtn.setOnClickListener {
            currentManager?.let { it1 -> changeNumbers(it1) }
        }

        binding.phoneNumberBtn.setOnClickListener {
            currentManager?.phoneNumber?.let { it1 -> makePhoneCall(it1) }
        }

        binding.whatsappMsgBtn.setOnClickListener {
            currentManager?.phoneNumber?.let { sendMessageToWhatsApp(requireContext(), it, "") }
        }

        binding.extraPhoneNumberBtn.setOnClickListener {
            currentManager?.secondNumber?.let { it1 -> makePhoneCall(it1) }
        }

        binding.extraPhoneWhatsappMsgBtn.setOnClickListener {
            currentManager?.secondNumber?.let { sendMessageToWhatsApp(requireContext(), it, "") }
        }
    }

    fun updateUi(manager: Manager){
        if (manager.secondNumber == null){
            binding.apply {
                extraPhoneLayout.isGone = true
                makeItMainNumberBtn.isGone = true
            }
        }

        if (manager.phoneNumber == null){
            binding.mainPhoneLayout.isGone = true
        }

        binding.apply {
            if (!manager.surname.isNullOrEmpty()){
                employeeNameTv.text = "${manager.firstName} ${manager.surname}"
            } else employeeNameTv.text = "${manager.firstName}"

            manager.phoneNumber?.let {
                phoneNumberTv.text = it
            }
            manager.secondNumber?.let {
                extraPhoneTv.text = it
            }
            manager.email?.let {
                emailTv.text = it
            }
            manager.comment?.let {
                commentTv.text = it
            }
        }
    }

    private fun changeNumbers(manager: Manager){
        var mainNumber = manager.phoneNumber?:""
        var extraNumber = manager.secondNumber?:""
        val bufferNumber: String?

        bufferNumber = mainNumber
        mainNumber = extraNumber
        extraNumber = bufferNumber

        currentManager?.copy(phoneNumber = mainNumber, secondNumber = extraNumber)?.let {
            viewModel.updateManagerToDb(it)
        }


    }


    private fun remove(id: Long){
        viewModel.remove(id)
    }

    private fun share(manager: Manager){
        val fullName =  listOfNotNull(manager.surname, manager.middleName, manager.firstName).joinToString (" ")
        val phoneNumber = manager.phoneNumber?.let { "тел.: ${manager.phoneNumber}" }
        val extraPhoneNumber = manager.phoneNumber?.let { "тел.: ${manager.phoneNumber}" }
        val email = manager.email?.let { "email: ${manager.email}" }

        val infoToShare = listOfNotNull(fullName, phoneNumber, extraPhoneNumber, email).joinToString(", ")

        requireContext().shareMessage(infoToShare)
    }

    private fun showMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.menu_remove)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item->
            when (item.itemId) {
                R.id.remove_menu_item -> {
                    showDeleteConfirmationDialog("сотрудника ${currentManager?.nameToShow}"){
                        currentManager?.let { remove(it.id) }
                    }
                }
                R.id.share_menu_item -> {
                    currentManager?.let { share(it) }
                }

                else -> Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            true
        })
        menu.show()
    }
}
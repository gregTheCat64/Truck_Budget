package ru.javacat.ui.new_employee

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
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
import ru.javacat.ui.databinding.FragmentNewEmployeeBinding
import ru.javacat.ui.utils.FragConstants

@AndroidEntryPoint
class NewEmployeeFragment : BaseFragment<FragmentNewEmployeeBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE

    private val viewModel: NewEmployeeViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentNewEmployeeBinding =
        { inflater, container ->
            FragmentNewEmployeeBinding.inflate(inflater, container, false)
        }

    private var isNeedToSet: Boolean = false

    private var firstName: String = ""
    private var surName: String? = null
    private var phoneNumber: String? = null
    private var secondPhoneNumber: String? = null
    private var email: String? = null
    private var comment: String? = null
    private var customerId: Long = 0L
    private var managerId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val args = arguments
        customerId = args?.getLong(FragConstants.COMPANY_ID) ?: 0
        isNeedToSet = arguments?.getBoolean(FragConstants.IS_NEED_TO_SET) ?: false
        Log.i("Log", "custmId: $customerId")

        //employeeId = null

        managerId = args?.getLong(FragConstants.MANAGER_ID)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_cancel_24)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        managerId?.let { viewModel.getManagerById(it)}

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.editedManager.collectLatest {
                    if (it != null && managerId != null) {
                        customerId = it.companyId
                        updateUi(it)
                    }
                }
            }
        }

        binding.actionBar.saveBtn.setOnClickListener {
            if (getFieldsData() && customerId != 0L) {
                saveManager(managerId ?: 0L, isNeedToSet)
            } else Toast.makeText(
                requireContext(),
                getString(R.string.fill_requested_fields),
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.actionBar.cancelBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getFieldsData(): Boolean {
        //val requestField = "Обязательное поле"

        binding.surName.text?.let {
            if (it.isNotEmpty()) surName = it.toString()
        }

        binding.phoneNumber.text?.let {
            if (it.isNotEmpty()) phoneNumber = it.toString()
        }

        secondPhoneNumber = binding.phoneNumber2.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        email = binding.email.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        comment = binding.comment.text?.let {
            if (it.isBlank()) null else it.toString()
        }

        if (binding.firstName.text.isNullOrEmpty()) {
            //binding.firstNameLayout.error = requestField
            binding.firstName.requestFocus()
            return false
        } else {
            firstName = binding.firstName.text.toString().trim()
            Log.i("NewEmplFrag", "firstname = $firstName")
        }
        return true
    }

    private fun updateUi(manager: Manager) {
        binding.apply {
            //(activity as AppCompatActivity).supportActionBar?.title = manager.nameToShow
            actionBar.title.text = getString(R.string.edit)
            manager.firstName.let {
                firstName.setText(it)
            }
            manager.surname?.let {
                surName.setText(it)
            }
            manager.phoneNumber?.let {
                phoneNumber.setText(it)
            }
            manager.secondNumber?.let {
                phoneNumber2.setText(it)
            }
            manager.email?.let {
                email.setText(it)
            }
            manager.comment?.let {
                comment.setText(it)
            }
        }
    }

    private fun saveManager(id: Long, isNeedToSet: Boolean) {
        val newEmployee = Manager(
            id, 0, false, customerId, firstName, null,
            surName, null, null, null, null,
            phoneNumber, secondPhoneNumber, email, comment
        )
        viewModel.saveNewManager(newEmployee, isNeedToSet)
        findNavController().navigateUp()

    }

}
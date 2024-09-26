package ru.javacat.ui.companies

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.domain.models.Company
import ru.javacat.domain.models.Manager
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.adapters.ManagerAdapter
import ru.javacat.ui.adapters.OnManagerListener
import ru.javacat.ui.databinding.FragmentCompanyBinding
import ru.javacat.ui.utils.FragConstants
import ru.javacat.ui.utils.makePhoneCall
import ru.javacat.ui.utils.sendMessageToWhatsApp
import ru.javacat.ui.utils.shareMessage
import ru.javacat.ui.utils.showDeleteConfirmationDialog

@AndroidEntryPoint
class CompanyFragment: BaseFragment<FragmentCompanyBinding>() {

    override var bottomNavViewVisibility: Int = View.GONE
    private var currentCompanyId: Long? = null
    private var currentCompany: Company? = null
    private lateinit var emplAdapter: ManagerAdapter
    private var isFavorite = false
    private val viewModel: CompanyViewModel by viewModels()

    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentCompanyBinding
        get() = { inflater, container ->
            FragmentCompanyBinding.inflate(inflater, container, false)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentCompanyId = arguments?.getLong(FragConstants.CUSTOMER_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        //(activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //(activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editBtn.setOnClickListener {
            val bundle = Bundle()
            if (currentCompanyId != null) {
                bundle.putLong(FragConstants.CUSTOMER_ID, currentCompanyId!!)
                findNavController().navigate(R.id.action_companyFragment_to_newCustomerFragment, bundle)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.moreBtn.setOnClickListener {
            showMenu(it)
        }

        binding.callCompanyPhoneBtn.setOnClickListener {
            binding.phoneNumberTv.text?.let {
                if (it.isNotEmpty()) makePhoneCall(it.toString())
            }
        }

        binding.whatsappCompanyMsgBtn.setOnClickListener {
            binding.phoneNumberTv.text?.let {
                if (it.isNotEmpty()) sendMessageToWhatsApp(requireContext(), it.toString(), "")
            }
        }

        binding.toTruckFleet.setOnClickListener {
            if (currentCompanyId != null) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.CUSTOMER_ID, currentCompanyId!!)
                findNavController().navigate(R.id.action_companyFragment_to_truckFleetViewPager, bundle)
            }
        }

        binding.addEmployeeBtn.setOnClickListener {
            if (currentCompanyId != null) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.COMPANY_ID,currentCompanyId!!)
                findNavController().navigate(R.id.action_companyFragment_to_newEmployeeFragment, bundle)
            }
        }

        emplAdapter = ManagerAdapter(object : OnManagerListener{
            override fun onManager(item: Manager) {
                val bundle = Bundle()
                bundle.putLong(FragConstants.MANAGER_ID, item.id)
                findNavController().navigate(R.id.action_companyFragment_to_newEmployeeFragment, bundle)
            }

            override fun onPhone(item: String?) {
                if (item != null) {
                    makePhoneCall(item)
                } else Toast.makeText(requireContext(), "Номер не найден", Toast.LENGTH_SHORT).show()
            }

            override fun onWhatsapp(item: String?) {
                if (item != null) {
                    sendMessageToWhatsApp(requireContext(), item, "")
                } else Toast.makeText(requireContext(), "Номер не найден", Toast.LENGTH_SHORT).show()
            }
        })

        binding.employeesRV.adapter = emplAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                currentCompanyId?.let { viewModel.getCustomerById(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.editedCustomer.collectLatest {customer->
                    customer?.let {
                        currentCompany = it
                        isFavorite = currentCompany!!.isFavorite
                        updateUi(it)
                    }
                }
            }
        }

        binding.favBtn.setOnClickListener {
            toggleFavorite()
        }
    }

    private fun updateUi(company: Company){
        binding.favBtn.icon = (if (isFavorite) ResourcesCompat.getDrawable(resources, R.drawable.baseline_favorite_24, null) else ResourcesCompat.getDrawable(resources, R.drawable.baseline_favorite_border_24, null))

        binding.apply {
            //(activity as AppCompatActivity).supportActionBar?.title = customer.shortName
            title.text = company.shortName
            customerNameTv.text = company.nameToShow
            shortNameTv.text = company.shortName
            company.atiNumber?.let {
                atiNumberTv.text = it.toString()
            }
            companyPhoneLayout.isGone = company.companyPhone==null
            company.companyPhone?.let {
                phoneNumberTv.text = it
            }

            formalAddressTv.text = company.formalAddress
            postAddressTv.text = company.postAddress
        }

        company.managers.let {
            emplAdapter.submitList(it)
        }

    }

    private fun removeCompany(){
        viewModel.hideCompanyById()
    }

    private fun share(c: Company){
        val name = c.nameToShow
        val ati = c.atiNumber?.let { "АТИ: $it" }
            //"ati: ${c.atiNumber}"
        val phone = c.companyPhone?.let { "телефон: $it" }
        val address =  c.postAddress?.let { "почтовый адрес: $it" }
        val formalAddress = c.formalAddress?.let{"юр. адрес: $it"}

        val infoToShare = listOfNotNull(name,ati, phone, address, formalAddress).joinToString(", ")

        requireContext().shareMessage(infoToShare)
    }

    private fun showMenu(view: View) {
        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.menu_remove)
        menu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item->
            when (item.itemId) {
                R.id.remove_menu_item -> {
                    showDeleteConfirmationDialog(currentCompany?.nameToShow.toString()){
                        currentCompanyId?.let { removeCompany() }
                        findNavController().navigateUp()
                    }

                }
                R.id.share_menu_item -> {
                    currentCompany?.let { share(it) }
                }

                else -> Toast.makeText(context, "Something wrong", Toast.LENGTH_SHORT).show()
            }
            true
        })
        menu.show()
    }

    private fun toggleFavorite() {
        val heartImageView = binding.favBtn
        // Выполнение анимации
        val scaleDownX = ObjectAnimator.ofFloat(heartImageView, "scaleX", 0.8f).apply {
            duration = 150
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
        }

        val scaleDownY = ObjectAnimator.ofFloat(heartImageView, "scaleY", 0.8f).apply {
            duration = 150
            repeatCount = 1
            repeatMode = ValueAnimator.REVERSE
        }

        // Запуск анимации
        AnimatorSet().apply {
            play(scaleDownX).with(scaleDownY)
            start()
        }

        // Изменение состояния и изображения сердца
        isFavorite = !isFavorite
        heartImageView.icon = (if (isFavorite) ResourcesCompat.getDrawable(resources, R.drawable.baseline_favorite_24, null) else ResourcesCompat.getDrawable(resources, R.drawable.baseline_favorite_border_24, null))
        viewModel.toggleFav(isFavorite)
    }
}
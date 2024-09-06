package ru.javacat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.databinding.ActivityMainBinding
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        viewModel.createDefaultCompany()

        val navView: BottomNavigationView = binding.bottomNav
        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_route_list,
            R.id.navigation_order_list,
            R.id.navigation_company_list,
            R.id.navigation_expense_list,
            R.id.navigation_stats
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{_,nd: NavDestination, _ ->
            if (
                nd.id == R.id.navigation_route_list ||
                nd.id == R.id.navigation_order_list ||
                nd.id == R.id.navigation_company_list ||
                nd.id == R.id.navigation_expense_list ||
                nd.id == R.id.navigation_stats
                ) navView.visibility = View.VISIBLE
            else navView.visibility = View.GONE
        }

//        binding.bottomNav.setOnItemSelectedListener {
//            when (it.itemId) {
//                R.id.navigation_route_list -> findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_route_list)
//                R.id.navigation_order_list -> findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_order_list)
//                R.id.navigation_customer_list -> findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_customer_list)
//                R.id.navigation_home_company -> {
////                    val bundle = Bundle()
////                    bundle.putLong(FragConstants.CUSTOMER_ID, FragConstants.MY_COMPANY_ID)
//                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_home_company)
//                }
//                else -> {}
//            }
//            true
//        }
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.title = "TruckBudget"

    }
    private fun replaceFragment(fragment: Fragment){
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
    }
}
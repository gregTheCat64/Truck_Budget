package ru.javacat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import ru.javacat.ui.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val navView: BottomNavigationView = binding.bottomNav
        val navController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_route_list,
            R.id.navigation_order_list,
            R.id.navigation_customer_list,
            R.id.navigation_home_company
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener{_,nd: NavDestination, _ ->
            if (
                nd.id == R.id.navigation_route_list ||
                nd.id == R.id.navigation_order_list ||
                nd.id == R.id.navigation_customer_list ||
                nd.id == R.id.navigation_home_company
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
    fun setBottomNavVisibility(visibility: Int) {
        binding.bottomNav.visibility = visibility
    }
}
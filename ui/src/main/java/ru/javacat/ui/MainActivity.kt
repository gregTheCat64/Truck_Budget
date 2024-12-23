package ru.javacat.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken
//import com.yandex.authsdk.YandexAuthLoginOptions
//import com.yandex.authsdk.YandexAuthOptions
//import com.yandex.authsdk.YandexAuthResult
//import com.yandex.authsdk.YandexAuthSdk
import dagger.hilt.android.AndroidEntryPoint
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.databinding.ActivityMainBinding
import ru.javacat.ui.utils.YandexAuthManager
import java.util.Calendar


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
//    private var tokenValue: String? = null
//    private lateinit var sdk: YandexAuthSdk
//    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


                        //навигация
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

    }

    
}
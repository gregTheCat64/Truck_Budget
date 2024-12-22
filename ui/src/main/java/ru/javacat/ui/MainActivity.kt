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

    val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private var tokenValue: String? = null
    private lateinit var sdk: YandexAuthSdk
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("Prefs", MODE_PRIVATE)
        val isNewUser = sharedPreferences.getBoolean("is_new_user", true)

            //Если юзер новый, создаем ему новую компанию
        if (isNewUser){
            Log.i(TAG, "it's a new user, making default company")
            viewModel.createDefaultCompany()
            sharedPreferences.edit().putBoolean("is_new_user", false).apply()
        }else {
            Log.i(TAG, "it's an old user, we use his db")
        }

                    //Авторизация Яндекс
        sdk = YandexAuthSdk.create(YandexAuthOptions(this))
        val launcher = registerForActivityResult(sdk.contract) { result -> handleResult(result) }
        val loginOptions = YandexAuthLoginOptions()

        if (!sharedPreferences.contains("yandex_token")){
            launcher.launch(loginOptions)
        } else {
            tokenValue = sharedPreferences.getString("yandex_token", null)
            println("tokenValue is gotten from prefs. It is $tokenValue")
        }

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

        lifecycleScope.launch {
            viewModel.user.collectLatest {
                Toast.makeText(this@MainActivity, "Welcome, mister ${it.display_name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //обработка результата
    private fun handleResult(result: YandexAuthResult) {
        Log.i(TAG, "handling result")
        when (result) {
            is YandexAuthResult.Success -> {
                println("success: ${result.token}")
                val token = result.token
                tokenValue = token.value
                if (token.value.isNotEmpty()){
                    sharedPreferences.edit().putString("yandex_token", token.value).apply()
                    viewModel.getUserInfo(tokenValue!!)
                    Log.i(TAG, "saving token to sharedPrefs")
                    Toast.makeText(this, "Authorization is successful", Toast.LENGTH_SHORT).show()
                }
            }
            is YandexAuthResult.Failure -> {
                Toast.makeText(this, "Authorization is failed, try again", Toast.LENGTH_SHORT).show()
                println("error: ${result.exception}")
            }
            YandexAuthResult.Cancelled -> {
                Toast.makeText(this, "Authorization is cancelled, be sure the App is authorized to have all your data saved remotely", Toast.LENGTH_LONG).show()
                println("canceled")
            }
        }
    }
    
}
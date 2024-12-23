package ru.javacat.ui.login

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LoginFragment: BaseFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentLoginBinding
        get() = {inflater, container->
            FragmentLoginBinding.inflate(inflater, container, false)
        }

    val viewModel: LoginViewModel by viewModels()
    private val TAG = "LoginFragment"
    private var tokenValue: String? = null
    private lateinit var sdk: YandexAuthSdk
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = activity?.getSharedPreferences("Prefs", MODE_PRIVATE)!!
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
        sdk = YandexAuthSdk.create(YandexAuthOptions(requireContext()))
        val launcher = registerForActivityResult(sdk.contract) { result -> handleResult(result) }
        val loginOptions = YandexAuthLoginOptions()

        binding.authBtn.setOnClickListener {
            if (!sharedPreferences.contains("yandex_token")){
                launcher.launch(loginOptions)
            } else {
                tokenValue = sharedPreferences.getString("yandex_token", null)
                println("tokenValue is gotten from prefs. It is $tokenValue")
                Toast.makeText(requireContext(), "Аккаунт уже авторизован", Toast.LENGTH_SHORT).show()
            }
        }


        lifecycleScope.launch {
            viewModel.user.collectLatest {
                binding.loginTv.setText("Welcome, mister ${it.display_name}")
                //Toast.makeText(requireContext(), "Welcome, mister ${it.display_name}", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "Authorization is successful", Toast.LENGTH_SHORT).show()
                }
            }
            is YandexAuthResult.Failure -> {
                Toast.makeText(requireContext(), "Authorization is failed, try again", Toast.LENGTH_SHORT).show()
                println("error: ${result.exception}")
            }
            YandexAuthResult.Cancelled -> {
                Toast.makeText(requireContext(), "Authorization is cancelled, be sure the App is authorized to have all your data saved remotely", Toast.LENGTH_LONG).show()
                println("canceled")
            }
        }
    }
}
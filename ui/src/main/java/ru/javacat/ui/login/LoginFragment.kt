package ru.javacat.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthSdk
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.javacat.ui.BaseFragment
import ru.javacat.ui.R
import ru.javacat.ui.databinding.FragmentLoginBinding
import ru.javacat.ui.utils.YandexAuthManager
import ru.javacat.ui.utils.YandexAuthResultHandler
import ru.javacat.ui.utils.load
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater, ViewGroup?) -> FragmentLoginBinding
        get() = { inflater, container ->
            FragmentLoginBinding.inflate(inflater, container, false)
        }

    val viewModel: LoginViewModel by viewModels()
    private val TAG = "LoginFragment"
    private val avatarFileName = "user_avatar.png"

    private lateinit var sharedPreferences: SharedPreferences

    private var isAuthorized = false
    private var isDbCreated = false
    private var hasImportedDb = false
    private var token: String? = null

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

        val userName = sharedPreferences.getString("user_name", null)

        isDbCreated = sharedPreferences.getBoolean("is_db_created", false)
        isAuthorized = sharedPreferences.getBoolean("authorized", false)
        hasImportedDb = sharedPreferences.getBoolean("hasImportedDb", false)
        val tokenValue = sharedPreferences.getString("yandex_token", null)
        token = tokenValue?:""


        if (isAuthorized) {
            Log.i(TAG, "it's an authorized user, starting app")
            //binding.loginTv.setText("Welcomen back, $userName!")
            //binding.userPicIv.load(requireContext(), avatarFileName)

            //binding.authLayout.isGone = true
            //binding.dbLayout.isGone = true
            //Каждый вход проверяем, авторизован ли юзер, если авторизован, идем дальше.
            //Если нет - даем выбор - авторизоваться или идти локально
            startApp()
        } else {
            //если не авторизован - виден выбор - продолжить локально или авторизоваться:
            binding.authLayout.isGone = false
            Log.i(TAG, "it's not authorized user, wait here")
        }

        val yandexAuthManager = YandexAuthManager(
            requireActivity() as AppCompatActivity,
            object : YandexAuthResultHandler {
                override fun onSuccess(tokenValue: String) {
                    if (tokenValue.isNotEmpty()) {
                        Log.i(TAG, "saving token to sharedPrefs")
                        //заносим в префы
                        sharedPreferences.edit().putString("yandex_token", tokenValue).apply()
                        sharedPreferences.edit().putBoolean("authorized", true).apply()
                        sharedPreferences.edit().putBoolean("hasImportedDb", true).apply()
                        sharedPreferences.edit().putBoolean("db_modified", false).apply()

                        //обновляем токен:
                        token = tokenValue
                        Toast.makeText(requireContext(), "Authorization successfull", Toast.LENGTH_SHORT).show()

                        if (!hasImportedDb){
                            //если это первый вход качаем инфу о юзере
                            viewModel.getUserInfo(tokenValue)
                            //а затем предлагаем
                            //скачать БД с я.Диска или использовать локальные данные для работы
                            binding.authLayout.isGone = true
                            binding.dbLayout.isGone = false
                        } else {
                            startApp()
                        }

                    }
                }

                override fun onFailure(exception: YandexAuthException) {
                    Toast.makeText(
                        activity,
                        "Authorization is failed, try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onCancel() {
                    Toast.makeText(
                        activity, "Authorization is cancelled, be sure the App is " +
                                "authorized to have all your data saved remotely", Toast.LENGTH_LONG
                    ).show()
                }
            }
        )


            //кнопка - скачать БД с яндекс диска
        binding.downloadDbBtn.setOnClickListener {
            if (token != null) {
                //Чтобы не путаться с поиском путей для БД и тд:
                //создаем компанию по умолчанию, чтобы создать на устройстве БД
                //а затем эту БД заменим новой скачавшейся с яндекса
                if (!isDbCreated){
                    createDb()
                }

                viewModel.downloadBdFromYandexDisk(
                    token!!
                ) { success ->
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            "All files downloaded successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        sharedPreferences.edit().putBoolean("is_db_created", true).apply()
                        findNavController().navigate(R.id.action_loginFragment_to_navigation_route_list)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to download some files, try to repeat",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

            //кнопка - использовать БД с устройства!
        binding.useDeviceDbBtn.setOnClickListener {
            startApp()
        }
            //кнопка продолжить локально!
        binding.localBtn.setOnClickListener {
            //sharedPreferences.edit().putBoolean("is_local_account", true).apply()
            startApp()
        }
            //кнопка - авторизоваться через Яндекс
        binding.yandexAuthBtn.setOnClickListener {
            if (!isAuthorized) {
                yandexAuthManager.startAuthorization()
                //launcher.launch(loginOptions)
            } else {
                println("tokenValue is gotten from prefs. It is $tokenValue")
                Toast.makeText(requireContext(), "Аккаунт уже авторизован", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest {
                isLoading->
                binding.progressBar.isGone = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.user.collectLatest {
                binding.loginTv.setText("Welcome, ${it.display_name}")
                sharedPreferences.edit().putString("user_name", it.display_name).apply()
                val userPic =
                    "https://avatars.yandex.net/get-yapic/${it.default_avatar_id}/islands-200"
                downloadAndSaveUserPic(requireContext(), userPic, avatarFileName) { success ->
                    if (success) {
                        println("Avatar downloaded and saved successfully.")
                        binding.userPicIv.load(requireContext(), avatarFileName)
                    } else {
                        println("Failed to download and save avatar.")
                    }
                }
                //Toast.makeText(requireContext(), "Welcome, mister ${it.display_name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadAndSaveUserPic(
        context: Context,
        url: String,
        fileName: String,
        callback: (Boolean) -> Unit
    ) {
        Glide.with(context)
            .load(url)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    val file = File(context.filesDir, fileName)
                    FileOutputStream(file).use { out ->
                        resource.toBitmap().compress(Bitmap.CompressFormat.PNG, 100, out)
                        callback(true)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    callback(false)
                }
            }
            )
    }

    private fun createDb(){
        viewModel.createDefaultCompany()
        sharedPreferences.edit().putBoolean("is_db_created", true).apply()
    }

    private fun startApp(){
        if (!isDbCreated){
            println("isdbcreated : $isDbCreated")
            //создаем дефолтную фирму только если устройство - новое!
           createDb()
        }
        findNavController().navigate(R.id.action_loginFragment_to_navigation_route_list)
    }
}
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
    //val path = "app:/backup.db"
    //private val newFilePath = "data/data/ru.javacat.truckbudget/databases/backup.db"
    //private val filePath = "data/data/ru.javacat.truckbudget/databases/app.db"

    //private var tokenValue: String? = null
    //private lateinit var sdk: YandexAuthSdk
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
        val userName = sharedPreferences.getString("user_name", null)
        val isAuthorized = sharedPreferences.getBoolean("authorized", false)
        //val isDbModified = sharedPreferences.getBoolean("db_modified", false)

        val tokenValue = sharedPreferences.getString("yandex_token", null)

        //Если юзер новый, создаем ему новую компанию
        //TODO вот тут подумать! - может сделать локальный профиль!
        if (isNewUser) {
            Log.i(TAG, "it's a new user, making default company")
            viewModel.createDefaultCompany()
            sharedPreferences.edit().putBoolean("is_new_user", false).apply()
        } else {
            Log.i(TAG, "it's an old user, we use his db")
            binding.loginTv.setText("Welcomen back, $userName!")
            binding.userPicIv.load(requireContext(), avatarFileName)
            findNavController().navigate(R.id.action_loginFragment_to_navigation_route_list)
        }


        val yandexAuthManager = YandexAuthManager(
            requireActivity() as AppCompatActivity,
            object : YandexAuthResultHandler {
                override fun onSuccess(tokenValue: String) {
                    if (tokenValue.isNotEmpty()) {
                        sharedPreferences.edit().putString("yandex_token", tokenValue).apply()
                        sharedPreferences.edit().putBoolean("authorized", true).apply()
                        viewModel.getUserInfo(tokenValue)
                        binding.authBtn.isGone = true
                        binding.startBtn.isGone = false
                        //Toast.makeText(requireContext(), "Welcome, $userName", Toast.LENGTH_SHORT).show()
                        viewModel.downloadBdFromYandexDisk(
                            tokenValue
                        ) { success ->
                            if (success) {
                                Toast.makeText(
                                    requireContext(),
                                    "All files downloaded successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //findNavController().navigate(R.id.action_loginFragment_to_navigation_route_list)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to download some files, try to repeat",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        Log.i(TAG, "saving token to sharedPrefs")
                        Toast.makeText(
                            requireContext(),
                            "Authorization is successful",
                            Toast.LENGTH_SHORT
                        ).show()
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

        binding.startBtn.setOnClickListener {
           findNavController().navigate(R.id.action_loginFragment_to_navigation_route_list)
        }

        binding.authBtn.setOnClickListener {
            if (!sharedPreferences.contains("yandex_token")) {
                yandexAuthManager.startAuthorization()
                //launcher.launch(loginOptions)
            } else {
                println("tokenValue is gotten from prefs. It is $tokenValue")
                Toast.makeText(requireContext(), "Аккаунт уже авторизован", Toast.LENGTH_SHORT)
                    .show()
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

        if (isAuthorized) {
            //findNavController().navigate(R.id.action_loginFragment_to_navigation_route_list)
        }


        binding.startBtn.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_navigation_route_list)
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


}
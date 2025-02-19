package ru.javacat.ui.utils

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken

interface YandexAuthResultHandler{
    fun onSuccess(tokenValue: String)
    fun onFailure(exception: YandexAuthException)
    fun onCancel()
}

class YandexAuthManager(
    private val activity: AppCompatActivity,
    private val handler: YandexAuthResultHandler
)
{
    private lateinit var yandexSdk: YandexAuthSdk
    private lateinit var authLauncher: ActivityResultLauncher<Intent>
    private val TAG = "YandexAuthManager"

    init {
        Log.i(TAG, "init")
        authLauncher = activity.registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()){ result->
            println("resultCode is ${result.resultCode}")
            handleResult(result.resultCode, result.data)
        }
    }

    fun startAuthorization(){
        Log.i(TAG, "startAuth")
        try {
            yandexSdk = YandexAuthSdk.create(YandexAuthOptions(activity))
            val loginOptions = YandexAuthLoginOptions()

            val intent: Intent = yandexSdk.contract.createIntent(activity, loginOptions)
            authLauncher.launch(intent)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun handleResult(resultCode: Int, data: Intent?){
        Log.i(TAG, "handleResult")
        try {
            val result = yandexSdk.contract.parseResult(resultCode, data)

            when (result ) {
                is YandexAuthResult.Success -> {
                    val token:YandexAuthToken = result.token
                    val tokenValue = token.value
                    handler.onSuccess(tokenValue)
                    println("success: ${token}")
                    println("expiresIn: ${token.expiresIn}")

                }
                is YandexAuthResult.Failure -> {
                    val error = result.exception
                    handler.onFailure(error)
                    println("error: ${error}")

                }
                is YandexAuthResult.Cancelled -> {
                    handler.onCancel()
                    println("Canceled")

                }
            }
        }catch (e: YandexAuthException){
            e.printStackTrace()
        }
    }
}
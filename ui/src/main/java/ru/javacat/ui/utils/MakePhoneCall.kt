package ru.javacat.ui.utils

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.makePhoneCall(phoneNumber: String){
    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
        // Если разрешение предоставлено
        val callIntent = Intent(Intent.ACTION_DIAL) // Используйте ACTION_CALL для осуществления звонка сразу
        callIntent.data = Uri.parse("tel:$phoneNumber")
        startActivity(callIntent)
    } else {
        //requestPermission(phoneNumber)
        Toast.makeText(requireContext(), "Нет разршенеия", Toast.LENGTH_SHORT).show()
    }
}

private fun Fragment.requestPermission(phoneNumber: String){
    requestPermissions(arrayOf(Manifest.permission.CALL_PHONE),
        REQUEST_CALL_PHONE)
    // Можно сохранить номер, чтобы использовать его после получения разрешения
    // Используйте ViewModel или другую подходящую структуру для сохранения состояния
}

private const val REQUEST_CALL_PHONE = 1
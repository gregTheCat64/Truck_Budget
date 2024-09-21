package ru.javacat.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.sendMessageToWhatsApp(context: Context, phoneNumber: String, message: String) {
    // Форматирование номера телефона и сообщения
    val formattedMessage = Uri.encode(message)
    val uri = Uri.parse("https://wa.me/$phoneNumber?text=$formattedMessage")

    // Создание интента для отправки сообщения в WhatsApp
    val intent = Intent(Intent.ACTION_VIEW, uri)

    // Проверка, доступно ли приложение WhatsApp
    if (intent.resolveActivity(requireContext().packageManager) != null) {
        startActivity(intent)
    } else {
        Toast.makeText(context, "WhatsApp не установлен", Toast.LENGTH_SHORT).show()
    }
}
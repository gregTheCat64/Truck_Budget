package ru.javacat.ui.utils

import android.content.Context
import android.content.Intent

fun Context.shareMessage(message: String) {
    // Создание интента для обмена сообщением
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }

    // Запуск диалога выбора приложения для обмена
    startActivity(Intent.createChooser(shareIntent, "Поделиться сообщением через"))
}
package ru.javacat.ui.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import ru.javacat.ui.R
import java.io.File

fun ImageView.load(context: Context, fileName: String) {
    val file = File(context.filesDir, fileName)
    if (file.exists()) {
        Glide.with(context)
            .load(file)
            .placeholder(R.drawable.baseline_people_alt_24)
            .circleCrop()

            .into(this)
    }
}
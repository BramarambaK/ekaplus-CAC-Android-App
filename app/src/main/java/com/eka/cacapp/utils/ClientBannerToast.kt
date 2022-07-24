package com.eka.cacapp.utils

import android.app.Activity
import android.graphics.*
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.eka.cacapp.R

/**
* For showing client banner after login
 * */

class ClientBannerToast {
    companion object {
        private lateinit var layoutInflater: LayoutInflater
        fun infoToast(context: Activity, message: String, imageBitmap: Bitmap, position: Int) {
            layoutInflater = LayoutInflater.from(context)
            val layout = layoutInflater.inflate(R.layout.banner_toast_layout, (context).findViewById(R.id.custom_toast_layout))
            layout.findViewById<ImageView>(R.id.custom_toast_image).setImageBitmap(imageBitmap)

            val drawable = ContextCompat.getDrawable(context, R.drawable.toast_round_bg)
            drawable?.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(context, R.color.white), PorterDuff.Mode.MULTIPLY)
            layout.background = drawable

            val toast = Toast(context.applicationContext)
            toast.duration = Toast.LENGTH_SHORT

            toast.setGravity(position, 0, 120)
            toast.view = layout
            toast.show()
        }
    }

}
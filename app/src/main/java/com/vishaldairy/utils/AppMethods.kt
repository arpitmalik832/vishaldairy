package com.vishaldairy.utils

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.vishaldairy.R

object AppMethods {

    fun formatPriceText(value: String): String? {
        return "\u20B9 $value"
    }

    fun formatNoOfProducts(noOfProducts: Int): String {
        return "$noOfProducts Products"
    }

    fun loadImage(context: Context?, url: String?, view: ImageView?, default: Int) {
        if (view == null) return
        context?.let {
            Glide.with(it)
                .load(if (!TextUtils.isEmpty(url)) url else "Arpit Malik's Unique invalid link.")
                .placeholder(if (default != 0) default else R.drawable.image_placeholder)
                .error(if (default != 0) default else R.drawable.image_placeholder)
                .into(view)
        }
    }

}
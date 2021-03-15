package com.shorbgy.petsshelter.utils

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ortiz.touchview.TouchImageView
import com.shorbgy.petsshelter.R


class ImageDialog {

    fun popupImageDialog(context: Context, imageUrl: String?) {
        val settingsDialog = Dialog(context)
        val imageView = TouchImageView(context)
        settingsDialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 800)
        imageView.layoutParams = params
        imageView.requestLayout()
        Glide.with(context)
            .load(imageUrl)
            .centerCrop()
            .apply(RequestOptions().override(600, 800))
            .placeholder(R.mipmap.person_placeholdr)
            .into(imageView)
        settingsDialog.setContentView(imageView, params)
        settingsDialog.show()
    }
}
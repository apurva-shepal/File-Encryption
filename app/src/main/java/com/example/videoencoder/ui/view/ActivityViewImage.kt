package com.example.videoencoder.ui.view

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.videoencoder.BuildConfig
import com.example.videoencoder.R
import com.example.videoencoder.constants.ApplicationConstants
import kotlinx.android.synthetic.main.activity_view_image_file.*
import java.io.File


class ActivityViewImage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image_file)
        setImage()
    }

    private fun setImage() {
        val intent = intent
        val imagePath = intent.getStringExtra(ApplicationConstants.IMAGE_PATH)
        val file = File(imagePath)
//        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        Glide.with(this)
            .load(file.absolutePath)
            .into(imageView)
    }
}
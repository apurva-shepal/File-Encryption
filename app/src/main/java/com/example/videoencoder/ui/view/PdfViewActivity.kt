package com.example.videoencoder.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.videoencoder.R
import com.example.videoencoder.constants.ApplicationConstants
import kotlinx.android.synthetic.main.activity_doc_view.*
import java.io.File

class PdfViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_view)
        initView()
    }

    private fun initView() {
        val pdfPath = intent.getStringExtra(ApplicationConstants.PDF_PATH)!!
        pdf_view.fromFile(File(pdfPath)).load()
    }
}
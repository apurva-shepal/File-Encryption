package com.example.videoencoder.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.videoencoder.R
import com.example.videoencoder.ui.`interface`.IOnFileSelected
import java.io.File

class FileNameAdapter(private val chapterList: ArrayList<File>, val iOnFileSelected: IOnFileSelected) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_file, parent, false)
        return ChapterViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chapterList.size
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val file = chapterList.get(position)

        if (holder is ChapterViewHolder) {
            holder.textView.text = file.name
            holder.textView.setOnClickListener {
                iOnFileSelected.onFileSelected(file)
            }
        }
    }

    internal class ChapterViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.encryptedFileName)

    }
}
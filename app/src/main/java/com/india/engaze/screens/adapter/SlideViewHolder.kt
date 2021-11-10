package com.india.engaze.screens.adapter

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.single_slide_layout.view.*

class SlideViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val download: ImageButton = view.file_single_download

    fun bind(title: String, date: String) {
        setTitle(title)
        setDate(date)
    }

    private fun setTitle(s: String) {
        view.file_single_title.text = s
    }

    private fun setDate(s: String) {
        view.file_single_date.text = s
    }
}
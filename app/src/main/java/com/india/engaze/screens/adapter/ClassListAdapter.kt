package com.india.engaze.screens.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.btp.me.classroom.Class.ClassAttribute
import com.india.engaze.R
import com.india.engaze.utils.CallBacks

class ClassListAdapter(private val classList: ArrayList<ClassAttribute>?, private val classes: ArrayList<ArrayList<String>>, private val classClickListener: CallBacks.ClassClickListener) : RecyclerView.Adapter<ClassViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ClassViewHolder {
        return ClassViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.single_classroom_layout, p0, false), p0.context)
    }

    override fun getItemCount(): Int {
        return classList?.size ?: classes.size
    }

    override fun onBindViewHolder(holder: ClassViewHolder, p: Int) {

        if(classList!=null){
            holder.bind(classList[p])
            holder.view.setOnClickListener {
                classClickListener.classClicked(classList[p].id)
            }
        }else{
            holder.bind(classes[p])
            holder.view.setOnClickListener {
                classClickListener.classClicked(classes[p][0])
            }
        }
    }

}
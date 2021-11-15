package com.india.engaze.screens.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.btp.me.classroom.Class.ClassAttribute
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.R
import com.india.engaze.screens.HomePage.MainActivity
import kotlinx.android.synthetic.main.single_classroom_layout.view.*
import java.util.ArrayList


class ClassViewHolder(val view: View, val ctx: Context) : RecyclerView.ViewHolder(view) {

    fun bind(list: ArrayList<String>) {

        setVisibility(false)

        FirebaseDatabase.getInstance().getReference("Classroom/${list[0]}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(data: DataSnapshot) {
                val classAttribute = ClassAttribute()
                classAttribute.id = list[0]
                classAttribute.registeredAs = list[1]
                classAttribute.status = data.child("status").value.toString()
                classAttribute.profileImage = data.child("thumbImage").value?.toString()
                        ?: data.child("image").value.toString()
                classAttribute.name = data.child("name").value.toString()

                classAttribute.timeTable = data.child("timeTable").value.toString()
                classAttribute.physicalStrength = data.child("physicalStrength").value.toString()
                classAttribute.membersCount = data.child("members").childrenCount.toInt()

                setVisibility(true)
                bind(classAttribute)
            }
        })
    }

    fun bind(_class: ClassAttribute) {

        with(_class) {
            setBackground(this.registeredAs)
            setName(this.name)
            setStatus(this.status)
            setProfileImage(this.profileImage)
            setRegisteredAs(this.registeredAs)
            view.timeTable.text = _class.timeTable
        }
    }

    private fun setVisibility(enable: Boolean) {
        view.visibility = when (enable) {
            true -> View.VISIBLE; else -> View.GONE
        }
    }

    private fun setBackground(registeredAs: String) {

        view.registeredAs.text = when (registeredAs) {
            "teacher" -> "You are teacher";
            "student" -> "You are student";
            else -> "You are not member"
        }

    }

    private fun setName(name: String) {
        view.class_single_name.text = name
    }

    private fun setStatus(status: String) {
        view.class_single_status.text = status
    }

    private fun setProfileImage(image: String) {
        val glideImage: Any = when (image) {
            "default", "null", "" -> R.drawable.ic_classroom
            else -> image
        }
        Glide.with(ctx).load(glideImage).into(view.class_single_image)
    }

    private fun setRegisteredAs(registeredAs: String) {
        val glideImage: Any = when (registeredAs) {
            "teacher" -> R.drawable.ic_teacher
            else -> R.drawable.ic_student
        }
        Glide.with(ctx).load(glideImage).into(view.class_single_registered_as)
    }
}
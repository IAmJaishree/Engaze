package com.india.engaze.screens.Members

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.BaseAdapter
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.R
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.base.BaseActivity
import kotlinx.android.synthetic.main.activity_class_member_info.*
import kotlinx.android.synthetic.main.toolbar_with_back.*

class MemberInfoActivity : BaseActivity() {

    private val mRootRef = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_member_info)


        toolbar_text.text = "Member info"
        backButton.setOnClickListener { onBackPressed() }

        val memberUID = intent.getStringExtra("userId") ?: return

        val registeredAs = intent.getStringExtra("registeredAs") ?: return
        val name = intent.getStringExtra("name") ?: return

        mRootRef.child("Users/$memberUID").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@MemberInfoActivity,"Error : ${p0.message}",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.value == null) {
                    Toast.makeText(this@MemberInfoActivity, "No such member in Database", Toast.LENGTH_SHORT).show()
                    return
                }

                class_member_main.visibility = View.VISIBLE
                class_member_info_name.text = name
                class_member_info_phone_number.text =  dataSnapshot.child("phone").value?.toString() ?: "Error"
                class_member_info_as.text = registeredAs.toUpperCase()

                val image = dataSnapshot.child("thumbImage").value?.toString()?: dataSnapshot.child("image").value.toString()
                val glideImage:Any = when(image) {"default","null" -> R.drawable.ic_default_profile else -> image}
                Glide.with(applicationContext).load(glideImage).into(class_member_info_image)
            }
        })
    }

    companion object {
        private const val TAG = "Member_Info_Activity"
    }
}

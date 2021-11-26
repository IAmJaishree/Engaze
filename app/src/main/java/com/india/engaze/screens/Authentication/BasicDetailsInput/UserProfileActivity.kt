package com.india.engaze.screens.Authentication.BasicDetailsInput

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.View.INVISIBLE
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.AppController
import com.india.engaze.R
import com.india.engaze.di.component.ActivityComponent
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.screens.slide.MyUploadingService
import kotlinx.android.synthetic.main.activity_join_class_request.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.toolbar_with_back.*

import kotlin.collections.HashMap

class UserProfileActivity : BaseActivity() {
    private val currentUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }
    private lateinit var mUserReference: DatabaseReference

    var userMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        initialize()


        user_profile_image.setOnClickListener {
            val gallaryIntent = Intent()
            gallaryIntent.type = "image/*"
            gallaryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(gallaryIntent, "Select Image"), 1)
        }
        user_profile_update_btn.setOnClickListener {
            val name = user_profile_name.text.toString()
            val status = user_profile_status.text.toString()

            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(status)) {
                userMap["name"] = name
                userMap["status"] = status
                userMap["register"] = "yes"
                registerUser()
            } else {
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun showLoading() {
        super.showLoading()
        user_profile_progressBar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
        user_profile_progressBar.visibility = View.GONE
    }

    private fun initialize() {

        teacherCheckbox.setOnCheckedChangeListener { buttonView, _ ->
            AppController.getInstance().getmSessionManager().isTeacher = (buttonView.isChecked)
        }

        toolbar_text.text = "User Profile Details"
        backButton.setOnClickListener{onBackPressed()}

        user_profile_scroll_view.visibility = INVISIBLE

        mUserReference = FirebaseDatabase.getInstance().getReference("Users/${currentUser!!.uid}")

        mUserReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                hideLoading()

                if (!dataSnapshot.exists()) {
                    user_profile_progressBar.visibility = INVISIBLE
                    user_profile_scroll_view.visibility = View.VISIBLE
                    return
                }

                val name = dataSnapshot.child("name").value.toString()
                val status = dataSnapshot.child("status").value.toString()

                val thumbsImgUri = dataSnapshot.child("thumbImage").value?.toString()?: dataSnapshot.child("image").value.toString()
                if(thumbsImgUri != "null")
                    Glide.with(applicationContext).load(thumbsImgUri).into(user_profile_image)

                user_profile_name.setText(name, TextView.BufferType.EDITABLE)
                user_profile_status.setText(status, TextView.BufferType.EDITABLE)

                val fcmToken = dataSnapshot.child("fcm-token").value.toString()

                userMap["name"] = name
                userMap["status"] = status
                userMap["fcm-token"] = fcmToken

                user_profile_progressBar.visibility = INVISIBLE
                user_profile_scroll_view.visibility = View.VISIBLE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                user_profile_progressBar.visibility = INVISIBLE
                user_profile_scroll_view.visibility = View.VISIBLE
                Toast.makeText(this@UserProfileActivity, "User_Profile Error : ${databaseError.message}", Toast.LENGTH_LONG).show()
                Log.d("chetan", "error : ${databaseError.message}")
            }
        })
    }


    private fun registerUser() {
        val sp = applicationContext.getSharedPreferences("me.chetan", Context.MODE_PRIVATE)
        val token = sp.getString("fcm-token", "null") ?: "null"

        userMap["fcm-token"] = token
        userMap["phone"] = currentUser!!.phoneNumber.toString()

        mUserReference.updateChildren(userMap.toMap()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                setDisplayName(userMap["name"])
                finish()
            } else {
                Log.d("chetan", "User_Profile_3 ${task.exception!!.toString()}")
                Toast.makeText(this, task.exception!!.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setDisplayName(s: String?) {
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(s)
                .build()

        currentUser!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("chetan", "User profile updated.")
            }
        }
    }

    private fun upload(uri: Uri) {
        val data = """{"image": ""}"""
        val uploadingIntent = Intent(this, MyUploadingService::class.java)

        uploadingIntent.putExtra("fileUri", uri)
        uploadingIntent.putExtra("storagePath", "UsersProfile/${currentUser!!.uid}")
        uploadingIntent.putExtra("databasePath", "Users/${currentUser!!.uid}")
        uploadingIntent.putExtra("data", data)
        uploadingIntent.putExtra("link", "image")

        uploadingIntent.action = MyUploadingService.ACTION_UPLOAD
        startService(uploadingIntent)
                ?: Log.d("chetan", "At this this no activity is running")
        showLoading()
        showMessage("Image sent for uploading.")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            if (requestCode == 1) {
                upload(data.data!!)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        userMap.clear()
    }
}
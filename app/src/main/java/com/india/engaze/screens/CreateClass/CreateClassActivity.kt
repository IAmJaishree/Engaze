package com.india.engaze.screens.CreateClass

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.R
import com.india.engaze.screens.slide.MyUploadingService
import kotlinx.android.synthetic.main.activity_create_class.*
import kotlinx.android.synthetic.main.toolbar_with_back.*

class CreateClassActivity : BaseActivity() {

    private val mCurrentUser by lazy { FirebaseAuth.getInstance().currentUser }
    private val mRootRef by lazy { FirebaseDatabase.getInstance().reference }

    private var userMap = HashMap<String, Any>()
    private val classId by lazy { mRootRef.child("Classroom").push().key }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_class)

        title = "Create New Class"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        initialize()

        create_class_image.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1)
        }

        create_Class_create_button.setOnClickListener {
            userMap["Classroom/$classId/name"] = create_class_name.text.toString()
            userMap["Classroom/$classId/status"] = create_class_status.text.toString()
            userMap["Classroom/$classId/timeTable"] = create_class_timetable_value.text.toString()
            userMap["Classroom/$classId/physicalStrength"] = create_class_physical_value.text.toString()

            if (!TextUtils.isEmpty(userMap["Classroom/$classId/name"].toString()) &&
                    !TextUtils.isEmpty(userMap["Classroom/$classId/status"].toString())) {
                createClass()
            } else {
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initialize() {

        toolbar_text.text = "Create Class"
        backButton.setOnClickListener{
            onBackPressed()
        }

        mRootRef.child("Classroom/$classId").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CreateClassActivity, "Error : ${databaseError.message}", Toast.LENGTH_LONG).show()
                Log.d("chetan", "Create_class error : ${databaseError.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val thumbsImgUri = dataSnapshot.child("thumbImage").value?.toString()?: dataSnapshot.child("image").value.toString()

                if (thumbsImgUri != "null")
                Glide.with(applicationContext).load(thumbsImgUri).into(create_class_image)
            }
        })
    }

    private fun createClass() {
        mRootRef.child("Users/${mCurrentUser?.uid}/name").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("chetan","create_class_1 Error: ${p0.message}")
                Toast.makeText(this@CreateClassActivity, "Failed : ${p0.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value == "null"){
                    Log.d("chetan","Error: name is not set")
                    Toast.makeText(this@CreateClassActivity, "Failed : name is not set", Toast.LENGTH_SHORT).show()
                }else{
                    userMap["Classroom/$classId/members/${mCurrentUser?.uid}/as"] = "teacher"
                    userMap["Classroom/$classId/members/${mCurrentUser?.uid}/name"] = p0.value.toString()
                    userMap["Class-Enroll/${mCurrentUser?.uid}/$classId/as"] = "teacher"

                    mRootRef.updateChildren(userMap.toMap()).addOnSuccessListener {
                        Toast.makeText(this@CreateClassActivity, "Class is successfully created.", Toast.LENGTH_LONG).show()
                        Log.d("chetan", "Class is successfully created")
                        finish()
                    }.addOnFailureListener { exception ->
                        Log.d("chetan", "create_class_2 Error ${exception.message}")
                        Toast.makeText(this@CreateClassActivity, exception.message, Toast.LENGTH_LONG).show()
                    }
                }
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK && data != null && data.data != null){
            if(requestCode == 1){
                upload(data.data!!)
            }
        }
    }

    private fun upload(uri: Uri) {
        val data = """{"image": ""}"""
        Log.d("chetan", "uploading Uri : $uri")
        val uploadingIntent = Intent(this, MyUploadingService::class.java)

        uploadingIntent.putExtra("fileUri", uri)
        uploadingIntent.putExtra("storagePath", "ClassProfile/$classId")
        uploadingIntent.putExtra("databasePath", "Classroom/$classId")
        uploadingIntent.putExtra("data", data)
        uploadingIntent.putExtra("link","image")

        uploadingIntent.action = MyUploadingService.ACTION_UPLOAD
        startService(uploadingIntent)
                ?: Log.d("chetan", "At this this no activity is running")
    }

}

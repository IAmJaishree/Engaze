package com.india.engaze.screens.CreateClass

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.R
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.slide.MyUploadingService
import kotlinx.android.synthetic.main.activity_create_class.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.toolbar_with_back.*

class CreateClassActivity : BaseActivity() {

    private val mCurrentUser by lazy { FirebaseAuth.getInstance().currentUser }
    private val mRootRef by lazy { FirebaseDatabase.getInstance().reference }

    private var userMap = HashMap<String, Any>()
    private val newClassId by lazy { mRootRef.child("Classroom").push().key }
    private var classIdd = "aaa"
    private var toCreate = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_class)

        toCreate = intent.extras?.get("toCreate") as Boolean

        classIdd = if(toCreate){
            newClassId.toString()
        }else{
            classId
        }

        if(!toCreate) create_Class_create_button.text = "Update";

        initialize()

        create_class_image.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1)
        }

        create_Class_create_button.setOnClickListener {
            userMap["Classroom/$classIdd/name"] = create_class_name.text.toString()
            userMap["Classroom/$classIdd/status"] = create_class_status.text.toString()
            userMap["Classroom/$classIdd/timeTable"] = create_class_timetable_value.text.toString()
            userMap["Classroom/$classIdd/physicalStrength"] = create_class_physical_value.text.toString()

            if (!TextUtils.isEmpty(userMap["Classroom/$classIdd/name"].toString()) &&
                    !TextUtils.isEmpty(userMap["Classroom/$classIdd/status"].toString())) {
                createClass()
            } else {
                Toast.makeText(this, "Fields can not be empty", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun showLoading() {
        super.showLoading()
        create_class_progress_bar.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        super.hideLoading()
        create_class_progress_bar.visibility = View.GONE
    }

    private fun initialize() {

        toolbar_text.text = "Create Class"
        backButton.setOnClickListener{
            onBackPressed()
        }

        mRootRef.child("Classroom/$classIdd").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CreateClassActivity, "Error : ${databaseError.message}", Toast.LENGTH_LONG).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val thumbsImgUri = dataSnapshot.child("thumbImage").value?.toString()?: dataSnapshot.child("image").value.toString()

                hideLoading()
                if(toCreate)return

                if (thumbsImgUri != "null")
                Glide.with(applicationContext).load(thumbsImgUri).into(create_class_image)

                create_class_name.setText(dataSnapshot.child("name").value.toString())
                create_class_status.setText(dataSnapshot.child("status").value.toString())
                create_class_timetable_value.setText(dataSnapshot.child("timeTable").value.toString())
                create_class_physical_value.setText(dataSnapshot.child("physicalStrength").value.toString())
            }
        })
    }

    private fun createClass() {
        mRootRef.child("Users/${mCurrentUser?.uid}/name").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@CreateClassActivity, "Failed : ${p0.message}", Toast.LENGTH_SHORT).show()
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.value == "null"){
                    Toast.makeText(this@CreateClassActivity, "Failed : name is not set", Toast.LENGTH_SHORT).show()
                }else{
                    userMap["Classroom/$classIdd/members/${mCurrentUser?.uid}/as"] = "teacher"
                    userMap["Classroom/$classIdd/members/${mCurrentUser?.uid}/name"] = p0.value.toString()
                    userMap["Class-Enroll/${mCurrentUser?.uid}/$classIdd/as"] = "teacher"

                    mRootRef.updateChildren(userMap.toMap()).addOnSuccessListener {
                        if(toCreate){
                            Toast.makeText(this@CreateClassActivity, "Class is successfully created.", Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(this@CreateClassActivity, "Class Details successfully updated.", Toast.LENGTH_LONG).show()
                        }
                        finish()
                    }.addOnFailureListener { exception ->
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
        val uploadingIntent = Intent(this, MyUploadingService::class.java)

        uploadingIntent.putExtra("fileUri", uri)
        uploadingIntent.putExtra("storagePath", "ClassProfile/$classIdd")
        uploadingIntent.putExtra("databasePath", "Classroom/$classIdd")
        uploadingIntent.putExtra("data", data)
        uploadingIntent.putExtra("link","image")

        uploadingIntent.action = MyUploadingService.ACTION_UPLOAD
        startService(uploadingIntent)
                ?: Log.d("js", "At this this no activity is running")

        showLoading()
        showMessage("Image sent for uploading.")
    }

}

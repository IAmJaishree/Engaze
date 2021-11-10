package com.india.engaze.repository.Firebase

import android.content.Intent
import android.util.Log
import com.btp.me.classroom.slide.SlideActivity
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.india.engaze.AppController
import com.india.engaze.screens.Authentication.BasicDetailsInput.BasicDetailsInputActivity
import com.india.engaze.screens.HomePage.MainActivity
import com.india.engaze.utils.CallBacks
import com.india.engaze.utils.IntentResult
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_slide.*
import java.text.SimpleDateFormat
import java.util.*


class FireBaseRepository {
    var firebaseDatabase: FirebaseDatabase
    var mRootRef: DatabaseReference


    fun attachUserListener(fireCallback: CallBacks.FireCallback) {

        mRootRef.child("Users/${AppController.getInstance().firebaseUser!!.uid}/register").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                fireCallback.onError(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!(p0.exists() && p0.value == "yes")) {
                    fireCallback.onSuccess(p0);
                }
            }
        })
    }

    fun attachClassListener(fireCallback: CallBacks.FireCallback) {
        mRootRef.child("Class-Enroll/${AppController.getInstance().firebaseUser!!.uid}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                fireCallback.onError(p0.message);
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fireCallback.onSuccess(dataSnapshot);
            }
        })

    }

    fun attachClassAsListener(classId: String, fireCallback: CallBacks.FireCallback ) {
        mRootRef.child("Class-Enroll/${AppController.getInstance().firebaseUser!!.uid}/${MainActivity.classId}/as").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                fireCallback.onError(p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    fireCallback.onSuccess(dataSnapshot)
//                if (dataSnapshot.value == "teacher") {
//                    slide_upload_button.show()
//                    slide_upload_button.setOnClickListener {
//                        startActivityForResult(Intent.createChooser(IntentResult.forPDF(), "Select Document"), 0)
//                    }
//                } else {
//                    slide_upload_button.hide()
//                }
            }

        })
    }

    fun attachSlidesListener(classId: String, fireCallback: CallBacks.FireCallback) {
        mRootRef.child("Slide/${MainActivity.classId}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                fireCallback.onError(p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fireCallback.onSuccess(dataSnapshot);
            }
        })
    }

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        mRootRef = FirebaseDatabase.getInstance().reference
    }
}

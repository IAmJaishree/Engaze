package com.india.engaze.repository.Firebase

import com.google.firebase.database.*
import com.india.engaze.AppController
import com.india.engaze.screens.HomePage.MainActivity
import com.india.engaze.utils.CallBacks


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

    fun attachClassAsListener(fireCallback: CallBacks.FireCallback) {
        mRootRef.child("Class-Enroll/${AppController.getInstance().firebaseUser!!.uid}/${MainActivity.classId}/as").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                fireCallback.onError(p0.message)
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    fireCallback.onSuccess(dataSnapshot)
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

    fun getClassDetails(fireCallback: CallBacks.FireCallback) {
        mRootRef.child("Classroom/${MainActivity.classId}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                fireCallback.onError(p0.message)
            }

            override fun onDataChange(p0: DataSnapshot) {
                fireCallback.onSuccess(p0);
            }
        })
    }

    fun addUpdates(update: String, fireCallback: CallBacks.FireUpdate) {
        val currentTime = System.currentTimeMillis().toString()
        mRootRef.child("Classroom/${MainActivity.classId}/updates/$currentTime").setValue(update).addOnSuccessListener{
            fireCallback.onSuccess()
        }.addOnFailureListener{
            fireCallback.onError(it.message)
        }
    }

    fun removeUpdate(key: String, fireUpdate: CallBacks.FireUpdate) {
        mRootRef.child("Classroom/${MainActivity.classId}/updates/$key").removeValue().addOnSuccessListener{
            fireUpdate.onSuccess()
        }.addOnFailureListener{
            fireUpdate.onError(it.message)
        }
    }

    init {
        firebaseDatabase = FirebaseDatabase.getInstance()
        mRootRef = FirebaseDatabase.getInstance().reference
    }
}

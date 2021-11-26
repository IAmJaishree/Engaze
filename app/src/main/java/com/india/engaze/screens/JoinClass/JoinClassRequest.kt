package com.india.engaze.screens.JoinClass

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.R
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.screens.slide.MyUploadingService
import com.india.engaze.utils.FileBuilderNew
import com.india.engaze.utils.IntentResult
import kotlinx.android.synthetic.main.activity_join_class.*
import kotlinx.android.synthetic.main.activity_join_class_request.*
import kotlinx.android.synthetic.main.single_classroom_layout.view.*
import kotlinx.android.synthetic.main.single_slide_layout.*
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.android.synthetic.main.toolbar_home.toolbar_text
import kotlinx.android.synthetic.main.toolbar_with_back.*
import timber.log.Timber

class JoinClassRequest : BaseActivity() {

    private val mRootRef = FirebaseDatabase.getInstance().reference
    private val mCurrentUser by lazy { FirebaseAuth.getInstance().currentUser }

    private var certUploaded = false;

    private var id = "";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_class_request)

        id = intent.extras!!.getString("id", null)
        initialize()

        mRootRef.child("Join-Class-Request/${id}/${mCurrentUser!!.uid}").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(tt: DataSnapshot) {

                if(tt.exists()){
                    val details = tt.child("details")


                    if(details.exists()){
                        submitRequest.text = "Update Request"
                        reason.setText(details.child("reason").value.toString())

                        if((details.child("physical").value.toString()).contains("true")){
                            physicalCheckBox.isChecked = true
                        }else if((details.child("physical").value.toString()).contains("false")){
                            physicalCheckBox.isChecked = false
                        }
                    }

                    val snapshot = tt.child("covid-cert")

                    if (snapshot.exists() && snapshot.child("link").exists()) {

                        covidCertificateContainer.visibility = View.VISIBLE
                        file_single_title.text = snapshot.child("title").value.toString();
                        file_single_date.visibility = View.INVISIBLE

                        Timber.e(snapshot.key);
                        certUploaded = true;
                    } else {
                        covidCertificateContainer.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showMessage(error.message)
            }
        })
    }

    private fun initialize() {
        toolbar_text.text = "Join a Class";
        backButton.setOnClickListener {
            onBackPressed()
        }
        covidCertificateContainer.visibility = View.GONE
        attachCertButton.visibility = View.GONE

        file_single_date_label.visibility = View.GONE
        file_single_date.visibility = View.GONE
        attachCertButton.setOnClickListener {
            startActivityForResult(Intent.createChooser(IntentResult.forPDF(), "Select Document"), 0)
        }
        physicalCheckBox.setOnCheckedChangeListener { buttonView, _ ->
            if (buttonView.isChecked) {
                attachCertButton.visibility = View.VISIBLE
            } else {
                attachCertButton.visibility = View.GONE
            }
        }
        submitRequest.setOnClickListener {
            if (!physicalCheckBox.isChecked || certUploaded) {
                if (reason.text.toString().isEmpty()) {
                    showMessage("Please enter a valid reason to attend this class");
                } else {
                    joinClass(id);
                }
            } else {
                showMessage("Upload Covid certificate for physical classes");
            }
        }
    }

    private fun joinClass(id: String) {
        val map = HashMap<String, String>()
        map["as"] = "student"
        map["request"] = "pending"
        map["reason"] = reason.text.toString()
        map["rollNumber"] = mCurrentUser!!.uid
        map["physical"] = physicalCheckBox.isChecked.toString()
        map["name"] = mCurrentUser!!.displayName.toString()


        mRootRef.child("Join-Class-Request/$id/${mCurrentUser!!.uid}/details").setValue(map).addOnSuccessListener {
            showMessage("requested")
            finish()
        }.addOnFailureListener { exception ->
            showMessage(exception.message)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null && data.data != null) {
            val uri = data.data ?: return
            val uploadingIntent = Intent(this, MyUploadingService::class.java)
            FileBuilderNew.upload(uri, this@JoinClassRequest , uploadingIntent, "Join-Class-Request/${id}/${mCurrentUser!!.uid}/covid-cert")
        } else {
            showMessage("PDF can't be retrieve.");
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val TAG = "Join_Class"
    }
}

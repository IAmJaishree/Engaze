package com.india.engaze.screens.assignment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.india.engaze.utils.Assignment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.india.engaze.R
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.screens.slide.MyUploadingService
import com.india.engaze.utils.IntentResult
import kotlinx.android.synthetic.main.activity_assignment_upload.*
import kotlinx.android.synthetic.main.toolbar_with_back.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AssignmentUploadActivity : BaseActivity() {

    private val mRootRef = FirebaseDatabase.getInstance().reference
    private val currentUser by lazy { FirebaseAuth.getInstance().currentUser }

    private var isAssignment = "Assignment"

    private var fileUri:Uri? = null

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_upload)

        initialize()

        assignment_upload_submit.setOnClickListener { _ ->

            val type = isAssignment

            val title = when {
                assignment_upload_title.text.isNotBlank() -> assignment_upload_title.text.toString()
                else -> {
                    assignment_upload_title.error = "Field can't be empty"
                    return@setOnClickListener
                }
            }

            val description = when {
                assignment_upload_description.text.isNotBlank() -> assignment_upload_description.text.toString()
                else -> {
                    null
                }
            }

            val maxMarks = when {
                assignment_upload_max_marks.text.isNotBlank() -> {
                    val marks = assignment_upload_max_marks.text.toString()
                    if(!marks.matches("\\d+".toRegex())){
                       assignment_upload_max_marks.error = "Accept only Number"
                        return@setOnClickListener
                    }else{
                        marks
                    }
                }
                else -> {
                    assignment_upload_max_marks.error = "Field can't be empty"
                    return@setOnClickListener
                }

            }

            val assignment = Assignment(title, description, "submissionDate", maxMarks = maxMarks)

            assignment_upload_title.text.clear()
            assignment_upload_description.text.clear()
            assignment_upload_max_marks.text.clear()

            if(fileUri == null){
                val currentTime = System.currentTimeMillis()
                mRootRef.child("$type/$classId/$currentTime").setValue(assignment).addOnSuccessListener {
                    Toast.makeText(this, "Assignment is successfully uploaded", Toast.LENGTH_LONG).show()
                    finish()
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to upload Assignment\nError : ${exception.message}", Toast.LENGTH_LONG).show()
                    finish()
                }
            }else{
                upload(fileUri!!,assignment)
                finish()
            }
        }

    }

    private fun initialize(){
        backButton.setOnClickListener { onBackPressed() }
        toolbar_text.text = "Upload Assignment/Exam"

        assignment_upload_file.setOnClickListener {
            startActivityForResult(Intent.createChooser(IntentResult.forPDF(),"Select Document"),0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null && data.data != null) {
            fileUri = data.data
        } else {
            Toast.makeText(this, "PDF can't be retrieve.", Toast.LENGTH_LONG).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload(uri: Uri, assignment: Assignment) {
        val data = Gson().toJson(assignment).toString()

        val uploadingIntent = Intent(this, MyUploadingService::class.java)

        val currentTime = System.currentTimeMillis().toString()
        val userId = currentUser!!.uid
        uploadingIntent.putExtra("fileUri", uri)
        uploadingIntent.putExtra("storagePath","Assignment/$classId/$userId/$currentTime")
        uploadingIntent.putExtra("databasePath","Assignment/$classId/$currentTime") ///todo backend : generate all student slist showing status as not complete
        uploadingIntent.putExtra("data",data)

        uploadingIntent.action = MyUploadingService.ACTION_UPLOAD
        startService(uploadingIntent)
                ?: Log.d("js", "At this this no activy is running")

        showMessage("Uploading started, check progress in notification")
    }

}
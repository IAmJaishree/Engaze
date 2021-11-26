package com.india.engaze.screens.assignment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.india.engaze.R
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.screens.slide.MyUploadingService
import com.india.engaze.utils.IntentResult
import com.india.engaze.utils.StudentAssignmentDetails
import kotlinx.android.synthetic.main.activity_assignment_details.*
import kotlinx.android.synthetic.main.single_students_marks_assignment_details.view.*
import kotlinx.android.synthetic.main.toolbar_with_back.*

private val currentUser by lazy { FirebaseAuth.getInstance().currentUser }
private val mRootRef = FirebaseDatabase.getInstance().reference
private const val REQUEST_CODE_ADD = 0

private var fileUri: Uri? = null
private var assignment: String = ""
private var maximumMarks = "0"

class AssignmentDetailsActivity : BaseActivity() {

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment_details)
        initialize()
        setAssignmentDetails()
    }

    private fun initialize() {
        title = "Assignment Details"
        backButton.setOnClickListener { onBackPressed() }
        toolbar_text.text = "Assignment Details"
        assignment = intent.getStringExtra("assignment")
    }

    private fun setAssignmentDetails() {
        mRootRef.child("Assignment/$classId/$assignment").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@AssignmentDetailsActivity, "Error : ${p0.message}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Assignment Error : ${p0.message}")
            }

            override fun onDataChange(database: DataSnapshot) {
                maximumMarks = database.child("maxMarks").value.toString()

                assignment_details_max_marks.text = maximumMarks
                assignment_details_title.text = database.child("title").value?.toString()
                assignment_details_description.text = database.child("description").value?.toString()

                if (database.child("link").value.toString() != "null") {
                    assignment_details_assignment_download_button.visibility = View.VISIBLE
                } else {
                    assignment_details_assignment_download_button.visibility = View.INVISIBLE
                }

                assignment_details_assignment_download_button.setOnClickListener {
                    val fileUrl = database.child("link").value.toString()
                    openWebPage(fileUrl);
                }

                mRootRef.child("Classroom/$classId/members").addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        Toast.makeText(this@AssignmentDetailsActivity, "Error : ${p0.message}", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "Assignment Error : ${p0.message}")
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val type = dataSnapshot.child("${currentUser!!.uid}/as").value.toString()

                        Log.d(TAG, "type : $type")

                        if (type == "teacher") {
                            assignment_details_marks.visibility = View.GONE

                            val marksList = ArrayList<StudentAssignmentDetails>()

                            for (member in database.child("marks").children) {
                                //if (dataSnapshot.child("${member.key.toString()}/as").value.toString() != "student") {
                                //  continue
                                //}
                                val studentAssignmentDetails = StudentAssignmentDetails(
                                        link = member.child("link").value.toString(),
                                        marks = member.child("marks").value.toString(),
                                        name = dataSnapshot.child("${member.key.toString()}/name").value.toString(),
                                        rollNumber = dataSnapshot.child("${member.key.toString()}/rollNumber").value.toString(),
                                        state = member.child("state").value.toString(),
                                        userId = member.key.toString(),
                                )

                                Log.d(TAG, "student assignment details : $studentAssignmentDetails")
                                marksList.add(studentAssignmentDetails)
                            }
                            if (marksList.size == 0) {
                                assignment_details_marks_linear_layout.visibility = View.GONE
                                assignment_details_marks_empty.visibility = View.VISIBLE
                            } else {
                                assignment_details_marks_linear_layout.visibility = View.VISIBLE
                                assignment_details_marks_empty.visibility = View.GONE
                                showStudentMarks(marksList)
                            }
                        } else if (type == "student") {
                            var myMarks = database.child("marks/${currentUser!!.uid}/marks").value.toString()
                            if (myMarks == "null")
                                myMarks = "0"

                            assignment_details_marks.text = "Marks Obtained : $myMarks"
                            assignment_details_marks_linear_layout.visibility = View.GONE
                            assignment_details_marks.visibility = View.VISIBLE
                            setSubmitButton()
                        }
                    }
                })
            }
        })
    }

    private fun getDialogBox(userId: String?){
        if (userId == null)
            return

        val editText = EditText(this)
        with(editText) {
            hint = "Marks"
            setEms(5)
            maxEms = 10
            inputType = InputType.TYPE_CLASS_NUMBER
        }

        val alertDialog = AlertDialog.Builder(this)

        with(alertDialog){
            setTitle("Enter Marks")
            setView(editText)
            alertDialog.setPositiveButton("Change") { _: DialogInterface, _: Int -> }
            alertDialog.setNegativeButton("Cancel"){ _: DialogInterface, _: Int -> }
        }

        val dialog = alertDialog.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
            val text = editText.text.toString()
            when {
                text.isEmpty() -> editText.error = "Can't be Empty"

                text.toLong() <= maximumMarks.toLong() -> {
                    mRootRef.child("Assignment/$classId/$assignment/marks/$userId/marks").setValue(text).addOnCompleteListener { task->
                        Log.d(TAG, "Update marks ${task.isSuccessful}")
                    }
                    dialog.dismiss()
                }

                else -> editText.error = "should be less than to Maximum Marks"
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener{
            dialog.cancel()
        }
    }

    private fun showStudentMarks(marksList: ArrayList<StudentAssignmentDetails>) {
        assignment_details_marks_recycler_view.setHasFixedSize(true)
        assignment_details_marks_recycler_view.layoutManager = LinearLayoutManager(this)

        val studentMarksAdapter = object : RecyclerView.Adapter<StudentMarksViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentMarksViewHolder {
                return StudentMarksViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_students_marks_assignment_details, parent, false))
            }

            override fun getItemCount() = marksList.size

            override fun onBindViewHolder(holder: StudentMarksViewHolder, position: Int) {
                holder.bind(marksList[position])

                 holder.view.single_student_marks_assignment_details_download_button.setOnClickListener {

                    val webpage = Uri.parse(marksList[position].link)
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }

                }
                holder.view.setOnClickListener { _->
                    getDialogBox(marksList[position].userId)
                }
            }
        }
        assignment_details_marks_recycler_view.adapter = studentMarksAdapter
    }

    private fun setSubmitButton() {

        mRootRef.child("Assignment/$classId/$assignment/marks/${currentUser!!.uid}/state").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@AssignmentDetailsActivity, "Error : ${p0.message}", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Assignment Error : ${p0.message}")
            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val state = dataSnapshot.value.toString()

                if (state.toLowerCase() == "submit") {
                    assignment_details_submit_button.text = "unsubmit"
                    assignment_details_add_button.visibility = View.GONE
                } else {
                    assignment_details_submit_button.text = "mark as done"
                    assignment_details_add_button.visibility = View.VISIBLE
                }
                assignment_details_linear_layout_submit.visibility = View.VISIBLE
            }
        })

        assignment_details_add_button.setOnClickListener {
            startActivityForResult(Intent.createChooser(IntentResult.forAll(), "Choose File"), REQUEST_CODE_ADD)
        }

        assignment_details_submit_button.setOnClickListener {
            onClickSubmitButton()
        }
    }

    private fun onClickSubmitButton() {

        val map = HashMap<String, String?>()

        when (assignment_details_submit_button.text.toString().toLowerCase()) {
            "mark as done" -> {
                map["state"] = "submit"
                map["link"] = null
                map["marks"] = null
            }
            "turn in" -> {
                map["state"] = "submit"
                map["link"] = fileUri.toString()
                map["marks"] = null
                if (fileUri != null) {
                    upload(fileUri!!, map)
                    Toast.makeText(this, "Check Notification for Result", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No File Found", Toast.LENGTH_SHORT).show()
                }
                return
            }
            "unsubmit" -> {
                map["state"] = "unsubmit"
                map["marks"] = null
                map["link"] = null
            }
            else -> {
                Log.d(TAG, "Invalid Text on Submit Button")
                return
            }
        }
        mRootRef.child("Assignment/$classId/$assignment/marks/${currentUser!!.uid}").updateChildren(map.toMap()).addOnSuccessListener {
            Toast.makeText(this, "Assignment Uploaded Successfully", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Assignment is successfully uploaded")
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error : ${exception.message}", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Assignment_1 Error : ${exception.message}")
        }

    }

    private fun upload(uri: Uri, map: HashMap<String, String?>) {
        Log.d(TAG, "uploading Uri : $uri")

        val data = Gson().toJson(map).toString()

        val uploadingIntent = Intent(this, MyUploadingService::class.java)

        val userId = currentUser!!.uid

        uploadingIntent.putExtra("fileUri", uri)
        uploadingIntent.putExtra("storagePath", "Assignment/$classId/$assignment/$userId")
        uploadingIntent.putExtra("databasePath", "Assignment/$classId/$assignment/marks/$userId")
        uploadingIntent.putExtra("data", data)

        uploadingIntent.action = MyUploadingService.ACTION_UPLOAD
        startService(uploadingIntent)
                ?: Log.d(TAG, "At this this no activity is running")
    }

    @SuppressLint("SetTextI18n")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_ADD && data != null && data.data != null) {
            fileUri = data.data
            assignment_details_submit_button.text = "Turn In"
        } else {
            Toast.makeText(this, "Can't Retrieve", Toast.LENGTH_SHORT).show()
        }
    }

    private class StudentMarksViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val download: ImageButton = view.single_student_marks_assignment_details_download_button

        fun bind(studentAssignmentDetails: StudentAssignmentDetails) {
            setName(studentAssignmentDetails.name)
            setMarks(studentAssignmentDetails.marks)

            download.setOnClickListener{
                if (studentAssignmentDetails.state == "submit") {
                    if (studentAssignmentDetails.link != "null") {
                        it.visibility = View.VISIBLE
                        it.isClickable = true
                        val fileUrl = studentAssignmentDetails.link
                            Log.d(TAG, "You have clicked ${studentAssignmentDetails.name}")
                           // view.context

                    } else {
                        it.visibility = View.INVISIBLE
                        it.isClickable = false
                    }
                } else {
                  //  it.setImageResource(R.drawable.ic_cross_red_24dp)
                    it.visibility = View.VISIBLE
                    it.isClickable = false
                }
            }
        }

        private fun setName(name: String?) {
            view.single_student_marks_assignment_details_name.text = name
        }

        private fun setMarks(marks: String?) {
            if (marks == null || marks == "null")
                view.single_student_marks_assignment_details_marks.text = "0"
            else
                view.single_student_marks_assignment_details_marks.text = marks
        }
    }

    companion object {
        const val TAG = "Assignment_Details"
    }
}

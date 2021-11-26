package com.india.engaze.screens.assignment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.india.engaze.utils.Assignment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.india.engaze.screens.HomePage.MainActivity
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.base.BaseActivity
import kotlinx.android.synthetic.main.activity_assignment.*
import kotlinx.android.synthetic.main.single_assignment_layout.view.*
import com.india.engaze.R
import com.india.engaze.utils.TimeAgo
import kotlinx.android.synthetic.main.toolbar_with_back.*

class AssignmentActivity : BaseActivity() {

    private val currentUser by lazy { FirebaseAuth.getInstance().currentUser }
    private val mRootRef = FirebaseDatabase.getInstance().reference

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assignment)

        initialize()

    }

    private fun initialize() {
        title = "Assignment"

        toolbar_text.text = "Assignments and Exams"
        backButton.setOnClickListener { onBackPressed() }
        assignment_list.setHasFixedSize(true)
        assignment_list.layoutManager = LinearLayoutManager(this)

        initializeFloatingButton()
    }

    private fun initializeFloatingButton() {
        mRootRef.child("Class-Enroll/${currentUser!!.uid}/$classId/as").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.value == "teacher"){
                    assignment_upload_button.show()
                    assignment_upload_button.setOnClickListener {
                        sendToAssignmentUploadActivity()
                    }
                }else{
                    assignment_upload_button.hide()
                }
            }

        })
    }

    override fun onStart() {
        super.onStart()

        if(classId == "null"){
            sendToMainActivity()
            return
        }

        val assignmentList = ArrayList<Assignment>()

        val assignmentAdapter = object : RecyclerView.Adapter<AssignmentViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
                //                Log.d(TAG,"View Type: ${viewType.toString()}")

                return AssignmentViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_assignment_layout, parent, false))
            }

            override fun getItemCount() = assignmentList.size

            override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
                holder.bind(assignmentList[position])
                holder.view.setOnClickListener {
                    sendToAssignmentDetails(assignmentList[position].uploadingDate)
                }
            }

        }

        mRootRef.child("Assignment/$classId").addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d("chetan", "Database Reference for Assignment is on cancelled, ${p0.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                assignmentList.clear()

                for(assignmentDataSnapshot in dataSnapshot.children){
                    if(assignmentDataSnapshot == null)continue

                    val assignment = Assignment(
                            title = assignmentDataSnapshot.child("title").value.toString(),
                            description = assignmentDataSnapshot.child("description").value.toString(),
                            submissionDate = "",
                            uploadingDate = assignmentDataSnapshot.key,
                            uploadDate =  TimeAgo.formatDateTime(assignmentDataSnapshot.key!!.toLong()),
                    )
                    assignmentList.add(assignment)
                }

                if(assignmentList.size == 0) {
                    assignment_empty.visibility = View.VISIBLE
                    assignment_empty.visibility = View.GONE
                }
                else {
                    assignment_empty.visibility = View.GONE
                    assignment_list.visibility = View.VISIBLE
                    assignment_list.adapter = assignmentAdapter
                }
            }
        })
    }

    private fun sendToAssignmentDetails(assignment: String?) {
        if (classId == "null") {
            sendToMainActivity()
            return
        }

        if (assignment == null){
            return
        }

        val intent = Intent(this, AssignmentDetailsActivity::class.java)
        intent.putExtra("assignment",assignment)
        startActivity(intent)
    }

    private fun sendToAssignmentUploadActivity() {
        if (classId == "null") {
            sendToMainActivity()
            return
        }

        startActivity(Intent(this, AssignmentUploadActivity::class.java))
    }

    private fun sendToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private class AssignmentViewHolder(val view:View):RecyclerView.ViewHolder(view){
        fun bind(assignment: Assignment) {
            with(assignment) {
                setTitle(this.title)
                setDescription(this.description)
                view.file_single_date.text  = assignment.uploadDate
            }
        }

        private fun setTitle(string:String?){

            view.single_assignment_title.text = when(string){
                "null" -> null
                else -> string
            }
        }

        private fun setDescription(string:String?){
            view.single_assignment_description.text = when(string){
                "null" -> null
                else -> string
            }
        }


    }

    companion object {
        const val TAG = "Assignment_Activity"
    }


}

package com.india.engaze.screens.PendingClassRequest

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.base.BaseActivity
import kotlinx.android.synthetic.main.activity_pending_request.*
import com.india.engaze.R
import kotlinx.android.synthetic.main.single_pending_request.view.*

class PendingRequestActivity : BaseActivity() {

    private val mRootRef = FirebaseDatabase.getInstance().reference

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_request)

        title = "Pending Requests"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        pending_request_list.setHasFixedSize(true)
        pending_request_list.layoutManager = LinearLayoutManager(this)

        val pendingRequestList = ArrayList<ArrayList<String>>()

        val pendingRequestAdapter = object : RecyclerView.Adapter<PendingRequestViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingRequestViewHolder {
                return PendingRequestViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.single_pending_request,parent, false),this@PendingRequestActivity)
            }

            override fun getItemCount() = pendingRequestList.size

            override fun onBindViewHolder(holder: PendingRequestViewHolder, position: Int) {
                holder.bind(pendingRequestList[position])
            }

        }

        mRootRef.child("Join-Class-Request/$classId").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@PendingRequestActivity,"Error : ${p0.message}",Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pendingRequestList.clear()

                for (user in dataSnapshot.children){
                    if(user == null)
                        continue

                    val array =  ArrayList<String>()
                    array.add(user.child("as").value.toString())
                    array.add(user.child("rollNumber").value.toString())
                    array.add(user.child("name").value.toString())
                    array.add(user.key.toString())

                    pendingRequestList.add(array)
                }

                if(pendingRequestList.size == 0){
                    pending_request_list.visibility = View.GONE
                    pending_request_empty.visibility = View.VISIBLE
                }else{
                    pending_request_list.visibility = View.VISIBLE
                    pending_request_empty.visibility = View.GONE
                    pending_request_list.adapter = pendingRequestAdapter
                }
            }
        })


    }

    private class PendingRequestViewHolder(val view: View, val context:Context) :RecyclerView.ViewHolder(view){

        val acceptButton:Button = view.single_pending_request_accept_button
        val rejectButton: Button = view.single_pending_request_reject_button
        val nameView: TextView = view.single_pending_request_name
        val rollNumberView: TextView = view.single_pending_request_roll_number


        fun bind(list:ArrayList<String>){

            //as, rollNumber, name, uid

            bind(list[2],list[1])
            onClick(list[2], list[3])
        }

        fun bind(name: String, rollNumber: String){
            setName(name)
            setRollNumber(rollNumber)
        }

        private fun setName(name: String){
            nameView.text = name
        }

        private fun setRollNumber(rollNumber:String){
            if (rollNumber == "null"){
                rollNumberView.visibility = View.GONE
                view.single_pending_request_as.text = "Teacher"
            }else {
                rollNumberView.text = rollNumber
            }
        }

        private fun onClick(name :String, userId: String){
            view.single_pending_request_accept_button.setOnClickListener{ acceptRejectOnClick(name, userId, "accept") }
            view.single_pending_request_reject_button.setOnClickListener{ acceptRejectOnClick(name, userId, "reject") }
        }

        private fun acceptRejectOnClick(name: String, userId:String,request: String) {
            acceptButton.isEnabled = false
            rejectButton.isEnabled = false

            if(request == "accept"){
                val userMap = HashMap<String, Any>()
                userMap["Classroom/$classId/members/$userId/as"] = "student"
                userMap["Classroom/$classId/members/$userId/name"] = name
                userMap["Class-Enroll/$userId/$classId/as"] = "student"
                FirebaseDatabase.getInstance().reference.updateChildren(userMap.toMap()).addOnSuccessListener {
                    deleteRequest(userId, request)
                }.addOnFailureListener { exception ->
                    Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }else{
                deleteRequest(userId, request)
            }
        }

        private fun deleteRequest(userId: String, request: String) {
            FirebaseDatabase.getInstance().getReference("Join-Class-Request/$classId/$userId").setValue(null).addOnSuccessListener {
                Toast.makeText(context, "Request $request Successfully", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener { exception ->
                Toast.makeText(context, "Error : ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
package com.india.engaze.screens.JoinClass

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.btp.me.classroom.Class.ClassAttribute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.R
import com.india.engaze.screens.adapter.ClassViewHolder
import com.india.engaze.screens.base.BaseActivity
import kotlinx.android.synthetic.main.activity_join_class.*
import kotlinx.android.synthetic.main.single_classroom_layout.view.*
import kotlinx.android.synthetic.main.toolbar_home.*
import kotlinx.android.synthetic.main.toolbar_home.toolbar_text
import kotlinx.android.synthetic.main.toolbar_with_back.*

class JoinClass : BaseActivity() {

    private val mRootRef = FirebaseDatabase.getInstance().reference
    private val mCurrentUser by lazy { FirebaseAuth.getInstance().currentUser }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_class)
        initialize()
    }

    private fun initialize(){
        toolbar_text.text = "Join a Class";
        backButton.setOnClickListener {
            onBackPressed()
        }
        join_class_list.layoutManager = LinearLayoutManager(this)
    }


    override fun onStart() {
        super.onStart()

        val classList = ArrayList<ClassAttribute>()
        val classListAdapter = object : RecyclerView.Adapter<ClassViewHolder>(){
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ClassViewHolder {
                return ClassViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.single_classroom_layout, p0, false), applicationContext)
            }

            override fun getItemCount() = classList.size

            override fun onBindViewHolder(holder: ClassViewHolder, p: Int) {
                holder.bind(classList[p])
                holder.view.setOnClickListener{
                    createJoinRequestActivity(classList[p].id)
                }
            }

        }
        mRootRef.child("Classroom").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "Error : ${p0.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                classList.clear()
                for (data in dataSnapshot.children){
                    if(data.hasChild("name")){
                        if(data.hasChild("members") && data.child("members").hasChild(mCurrentUser!!.uid))
                            continue
                        val classAttribute = ClassAttribute(
                                data.key.toString(),
                                data.child("name").value.toString(),
                                data.child("status").value.toString(),
                                data.child("thumbImage").value?.toString() ?: data.child("image").value.toString(),
                                registeredAs = "not",
                                timeTable = data.child("timeTable").value.toString()
                        )

                        classList.add(classAttribute)
                    }
                }

                if (classList.size == 0){
                    join_class_empty_list.visibility = View.VISIBLE
                    join_class_list.visibility = View.INVISIBLE
                }else{
                    join_class_empty_list.visibility = View.INVISIBLE
                    join_class_list.visibility = View.VISIBLE
                    join_class_list.adapter = classListAdapter
                }
            }

        })

    }


    private fun createJoinRequestActivity(id:String){
        val intent = Intent(this@JoinClass, JoinClassRequest::class.java);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    private fun getDialogBoxRollNumber(id:String){
        val rollNumberEditText = EditText(this)
        with(rollNumberEditText) {
            hint = "Roll Number"
            setEms(5)
            maxEms = 10
            minEms = 2
            gravity = View.TEXT_ALIGNMENT_CENTER
            inputType = InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        }

        val alertDialog = AlertDialog.Builder(this)

        with(alertDialog){
            setTitle("Enter Roll Number")
            setView(rollNumberEditText)
            alertDialog.setPositiveButton("Join") { _: DialogInterface, _: Int -> }
            alertDialog.setNegativeButton("Cancel"){_: DialogInterface,_: Int -> }
        }

        val dialog = alertDialog.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val rollNumber = rollNumberEditText.text.toString()
            if(rollNumber.isNotEmpty()){
                joinClass(id, rollNumber)
                dialog.dismiss()
            }else{
                rollNumberEditText.error = "Can't be Empty"
            }
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener{
            dialog.cancel()
        }
    }

    private fun joinClass(id:String, rollNumber:String){
        val map = HashMap<String, String>()
        map["as"] = "student"
        map["request"] = "pending"
        map["rollNumber"] = rollNumber
        map["name"] = mCurrentUser!!.displayName.toString()
        mRootRef.child("Join-Class-Request/$id/${mCurrentUser!!.uid}").setValue(map).addOnSuccessListener {
            Log.d(TAG, "Request send")
            Toast.makeText(this@JoinClass, "Request send", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Log.d(TAG, " Join Class Error : ${exception.message}")
        }
    }


    companion object {
        private const val TAG = "Join_Class"
    }
}

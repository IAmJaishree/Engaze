package com.india.engaze.screens.Members

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.india.engaze.R
import com.india.engaze.screens.HomePage.MainActivity
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.base.BaseActivity
import kotlinx.android.synthetic.main.activity_class_members.*
import kotlinx.android.synthetic.main.single_people_layout.view.*
import kotlinx.android.synthetic.main.toolbar_with_back.*

class ClassMembersActivity: BaseActivity() {
    private val mRootRef = FirebaseDatabase.getInstance().reference
    private val currentUser by lazy { FirebaseAuth.getInstance().currentUser }
    private var isTeacher: Boolean = false

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_members)

        toolbar_text.text = "Members"
        backButton.setOnClickListener { onBackPressed() }

        peoples_teacher_list.layoutManager = LinearLayoutManager(this)
        peoples_students_list.layoutManager = LinearLayoutManager(this)

        peoples_teacher_list.setHasFixedSize(true)
        peoples_students_list.setHasFixedSize(true)

        val teachersList = ArrayList<HashMap<String,String>>()
        val studentsList = ArrayList<HashMap<String,String>>()

        mRootRef.child("Classroom/$classId/members").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG,"Database Reference for People on data changed, ${p0.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                teachersList.clear();studentsList.clear()

                for (people in dataSnapshot.children){
                    if (people.value == null)
                        continue

                    val map = HashMap<String,String>()
                    map["userId"] = people.key.toString()
                    map["as"] = people.child("as").value.toString()
                    map["name"] = people.child("name").value.toString()
                    map["rollNumber"] = people.child("rollNumber").value.toString()

                    if(currentUser!!.uid == map["userId"]) {
                        map["who"] = "me"
                       isTeacher = map["as"] == "teacher"
                    }

                    when(map["as"]){
                        "teacher" -> teachersList.add(map)
                        "student" -> studentsList.add(map)
                    }
                }

                showList(teachersList, studentsList)
            }
        })
    }

    private fun showList(teachersList:List<HashMap<String, String>>, studentsList:List<HashMap<String, String>>){

        if(peoples_teacher_list != null && teachersList.isNotEmpty()){
            peoples_teacher_list.visibility = View.VISIBLE
            peoples_teacher_empty.visibility = View.GONE
        }else{
            peoples_teacher_list.visibility = View.GONE
            peoples_teacher_empty.visibility = View.VISIBLE
        }
        if(peoples_students_list != null && studentsList.isNotEmpty()){
            peoples_students_list.visibility = View.VISIBLE
            peoples_students_empty.visibility = View.GONE
        }else{
            peoples_students_list.visibility = View.GONE
            peoples_students_empty.visibility = View.VISIBLE
        }

        val teacherAdapter = object: RecyclerView.Adapter<PeopleViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PeopleViewHolder {
//                Log.d(TAG, "Teacher adapter on create viewHolder")
                return PeopleViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_people_layout, parent, false), this@ClassMembersActivity, isTeacher)
            }

            override fun getItemCount() = teachersList.size

            override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
                holder.bind(teachersList[position])
            }

        }

        val studentAdapter = object : RecyclerView.Adapter<PeopleViewHolder>(){
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): PeopleViewHolder {
                return PeopleViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_people_layout, parent, false), this@ClassMembersActivity, isTeacher)
            }

            override fun getItemCount() = studentsList.size

            override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
                holder.bind(studentsList[position])
            }
        }

        peoples_teacher_list.adapter = teacherAdapter
        peoples_students_list.adapter = studentAdapter
    }

    private class PeopleViewHolder(val view:View, val context: Context, val isTeacher:Boolean): RecyclerView.ViewHolder(view){

        fun bind(map: HashMap<String,String>){  
            setName(map["name"]!!)
            setCurrentUserIcon(map["who"])
            onClick(map["name"]!!, map["userId"]!!, map["rollNumber"]!!, map["as"]!!)
        }

        private fun setName(name:String){
            view.single_people_name.text = name
        }

        private fun setCurrentUserIcon(me:String?){
            if(me == "me")
                view.single_people_current_user.visibility = View.VISIBLE
            else
                view.single_people_current_user.visibility = View.INVISIBLE
        }

        private fun onClick(name:String, userId:String,rollNumber: String, registeredAs:String){

            view.setOnClickListener{
                val memberInfotainment = Intent(context, MemberInfoActivity::class.java)
                memberInfotainment.putExtra("userId", userId)
                memberInfotainment.putExtra("registeredAs", registeredAs)
                memberInfotainment.putExtra("rollNumber", rollNumber)
                memberInfotainment.putExtra("name", name)
                context.startActivity(memberInfotainment)
            }
        }
    }

    companion object {
        private const val TAG = "chetan"
    }

}
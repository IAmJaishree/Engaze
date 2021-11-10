package com.india.engaze.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.btp.me.classroom.Class.ClassAttribute
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.india.engaze.R
import com.india.engaze.screens.Authentication.BasicDetailsInput.BasicDetailsInputActivity
import com.india.engaze.screens.Splash.SplashActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.single_classroom_layout.view.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private val mCurrentUser: FirebaseUser?
        get() {
            return FirebaseAuth.getInstance().currentUser;
        }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this);
//        ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);

        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
        if (mCurrentUser == null) {
            sendToHomepage()
            return
        }




        main_class_list.setHasFixedSize(true)
        main_class_list.layoutManager = LinearLayoutManager(this)

        main_create_class.setOnClickListener { showToast("clicked") }

    }

    fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }



    override fun onStart() {
        super.onStart()

        if (mCurrentUser == null) {
            sendToHomepage()
            return
        }
        val mRootRef = FirebaseDatabase.getInstance().reference

        mRootRef.child("Users/${mCurrentUser!!.uid}/register").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "error : ${p0.message}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (!(p0.exists() && p0.value == "yes")) {
                    startActivity(Intent(this@MainActivity, BasicDetailsInputActivity::class.java))
                }
            }

        })

        val classList = ArrayList<ArrayList<String>>()

        val classListAdapter = object : RecyclerView.Adapter<ClassViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ClassViewHolder {
                return ClassViewHolder(LayoutInflater.from(p0.context)
                        .inflate(R.layout.single_classroom_layout, p0, false), applicationContext)
            }

            override fun getItemCount() = classList.size

            override fun onBindViewHolder(holder: ClassViewHolder, p: Int) {
                Log.d(TAG, "Class Adapter onBind : $classList[")
                holder.bind(classList[p])
                holder.view.setOnClickListener {
                    sendToClassHomeActivity(classList[p][0])
                }
            }
        }

        mRootRef.child("Class-Enroll/${mCurrentUser!!.uid}").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
//                Toast.makeText(this@MainActivity,"Error : ${p0.message}",Toast.LENGTH_SHORT).show()
                Log.d(TAG, "class-enroll on canceled ${p0.message}")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                classList.clear()

                for (group in dataSnapshot.children) {
                    if (group.value == null)
                        continue

                    val list = ArrayList<String>()
                    list.add(group.key.toString())
                    list.add(group.child("as").value.toString())

                    classList.add(list)
                }
                main_class_list.adapter = classListAdapter
            }

        })

    }

    private fun sendToClassHomeActivity(id: String) {

        val chatIntent = Intent(this, PublicChatActivity::class.java)
        classId = id
        startActivity(chatIntent)
    }

    private fun sendToCreateClassActivity() {
//        startActivity(Intent(this, CreateClassActivity::class.java))
    }

    private fun sendToHomepage() {
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        Log.d("chetan", "${item?.itemId} is clicked")

        item ?: return false

        when (item.itemId) {
            R.id.main_setting_btn -> {
                showToast("settings")
                startActivity(Intent(this, BasicDetailsInputActivity::class.java))
            }

            R.id.main_logout_btn -> {
                FirebaseAuth.getInstance().signOut()
                sendToHomepage()
            }

            R.id.main_join_class_btn -> {
                showToast("join class")
                startActivity(Intent(this, JoinClass::class.java))
            }
        }
        return true
    }

    private class ClassViewHolder(val view: View, val ctx: Context) : RecyclerView.ViewHolder(view) {

        fun bind(list: ArrayList<String>) {

            setVisibility(false)

            FirebaseDatabase.getInstance().getReference("Classroom/${list[0]}").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d(TAG, "Error : ${p0.message}")
                }

                override fun onDataChange(data: DataSnapshot) {
                    val classAttribute = ClassAttribute()
                    classAttribute.id = list[0]
                    classAttribute.registeredAs = list[1]
                    classAttribute.status = data.child("status").value.toString()
                    classAttribute.profileImage = data.child("thumbImage").value?.toString()
                            ?: data.child("image").value.toString()
                    classAttribute.name = data.child("name").value.toString()


                    setVisibility(true)
                    bind(classAttribute)
                }
            })
        }

        fun bind(_class: ClassAttribute) {
            Log.d("chetan", "ClassViewHolder")
            Log.d("chetan", "classid : ${_class.name}")
            Log.d("chetan", "classid : ${_class.id}")
            Log.d("chetan", "classid : ${_class.profileImage}")
            Log.d("chetan", "classid : ${_class.status}")


            with(_class) {
                setBackground(this.registeredAs)
                setName(this.name)
                setStatus(this.status)
                setProfileImage(this.profileImage)
                setRegisteredAs(this.registeredAs)

            }
        }

        private fun setVisibility(enable: Boolean) {
            view.visibility = when (enable) {
                true -> View.VISIBLE; else -> View.GONE
            }
        }

        private fun setBackground(registeredAs: String) {

            val gd = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"), Color.parseColor("#ffffff"), when (registeredAs) {
                "teacher" -> Color.parseColor("#FFA631"); else -> Color.parseColor("#00FFFF")
            }
            ))
            gd.cornerRadius = 0f
            view.background = gd
        }

        private fun setName(name: String) {
            view.class_single_name.text = name
        }

        private fun setStatus(status: String) {
            view.class_single_status.text = status
        }

        private fun setProfileImage(image: String) {
            Log.d(TAG, "image mian : $image")
            val glideImage: Any = when (image) {
                "default", "null", "" -> R.drawable.ic_classroom
                else -> image
            }
            Glide.with(ctx).load(glideImage).into(view.class_single_image)
        }

        private fun setRegisteredAs(registeredAs: String) {
            val glideImage: Any = when (registeredAs) {
                "teacher" -> R.drawable.ic_teacher
                else -> R.drawable.ic_student
            }
            Glide.with(ctx).load(glideImage).into(view.class_single_registered_as)
        }
    }

    companion object {
        private const val TAG = "chetan"
        var classId: String = "null"
    }


}
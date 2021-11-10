package com.india.engaze.screens.HomePage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.india.engaze.AppController
import com.india.engaze.R
import com.india.engaze.screens.Authentication.BasicDetailsInput.BasicDetailsInputActivity
import com.india.engaze.screens.Chat.PublicChatActivity
import com.india.engaze.screens.JoinClass
import com.india.engaze.screens.adapter.ClassViewHolder
import com.india.engaze.screens.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import javax.inject.Inject


class MainActivity : BaseActivity(), HomeContract.View {

    @Inject
    lateinit var mPresenter: HomeContract.Presenter<HomeContract.View>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activityComponent.inject(this);
        mPresenter.onAttach(this)
        initSetup();
    }

    private fun initSetup() {
        mPresenter.AttachFirebaseListeners();
        checkAndGetStoragePermission()
        setSupportActionBar(main_toolbar);
        main_class_list.setHasFixedSize(true)
        main_class_list.layoutManager = LinearLayoutManager(this)
        main_create_class.setOnClickListener { showToast("clicked") }
    }

    private fun checkAndGetStoragePermission() {
        val permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val REQUEST_EXTERNAL_STORAGE = 1
        val PERMISSIONS_STORAGE = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
        }
    }

    override fun showClasses(classList: ArrayList<ArrayList<String>>) {

        val classListAdapter = object : RecyclerView.Adapter<ClassViewHolder>() {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ClassViewHolder {
                return ClassViewHolder(LayoutInflater.from(p0.context)
                        .inflate(R.layout.single_classroom_layout, p0, false), applicationContext)
            }

            override fun getItemCount() = classList.size


            override fun onBindViewHolder(holder: ClassViewHolder, p: Int) {
                holder.bind(classList[p])
                holder.view.setOnClickListener {
                    sendToClassHomeActivity(classList[p][0])
                }
            }
        }

        main_class_list.adapter = classListAdapter
    }


    private fun sendToClassHomeActivity(id: String) {
        val chatIntent = Intent(this, PublicChatActivity::class.java)
        classId = id
        startActivity(chatIntent)
    }

    private fun sendToCreateClassActivity() {
//        startActivity(Intent(this, CreateClassActivity::class.java))
    }


    //Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)
        item ?: return false
        when (item.itemId) {
            R.id.main_setting_btn -> {
                showToast("settings")
                startActivity(Intent(this, BasicDetailsInputActivity::class.java))
            }

            R.id.main_logout_btn -> {
                AppController.getInstance().logout()
                sendToSplash()
            }

            R.id.main_join_class_btn -> {
                showToast("join class")
                startActivity(Intent(this, JoinClass::class.java))
            }
        }
        return true
    }

    companion object {
        var classId: String = "null"
    }


}
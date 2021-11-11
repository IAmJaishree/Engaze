package com.india.engaze.screens.HomePage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.india.engaze.R
import com.india.engaze.screens.Chat.PublicChatActivity
import com.india.engaze.screens.ClassActivity.ClassActivity
import com.india.engaze.screens.CreateClass.CreateClassActivity
import com.india.engaze.screens.adapter.BottomSheetNavigationFragment.Companion.newInstance
import com.india.engaze.screens.adapter.ClassListAdapter
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
        main_class_list.setHasFixedSize(true)
        main_class_list.layoutManager = LinearLayoutManager(this)

        bottom_app_bar.setOnClickListener {
            val bottomSheetDialogFragment: BottomSheetDialogFragment = newInstance()
            bottomSheetDialogFragment.show(supportFragmentManager, "Bottom Sheet Dialog Fragment")
        }
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
        val classListAdapter = ClassListAdapter(null, classList) { id ->
            if (id != null) {
                sendToClassHomeActivity(id)
            }
        }
        main_class_list.adapter = classListAdapter
    }


    private fun sendToClassHomeActivity(id: String) {
        classId = id
        val classActivityIntent = Intent(this, ClassActivity::class.java)
        classActivityIntent.putExtra("classId", classId);
        startActivity(classActivityIntent)
    }

    companion object {
        var classId: String = "null"
    }


}
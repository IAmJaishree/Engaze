package com.india.engaze.screens.slide

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.btp.me.classroom.Class.FileBuilderNew
import com.google.firebase.database.*
import com.india.engaze.R
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.adapter.SlideViewHolder
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.utils.IntentResult
import kotlinx.android.synthetic.main.activity_slide.*
import kotlinx.android.synthetic.main.single_slide_layout.view.*
import java.util.*
import javax.inject.Inject

class SlideActivity : BaseActivity(), SlideContract.View {


    @Inject
    lateinit var mPresenter: SlideContract.Presenter<SlideContract.View>

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide)
        activityComponent.inject(this);
        mPresenter.onAttach(this)
        initialize()
    }

    private fun initialize() {

        mPresenter.attachListeners(classId);

        title = "Slides"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        slide_list.setHasFixedSize(true)
        slide_list.layoutManager = LinearLayoutManager(this)

    }

    override fun showSlides(slideList: ArrayList<HashMap<String, String>>) {
        val slideAdapter = object : RecyclerView.Adapter<SlideViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SlideViewHolder {
                return SlideViewHolder(LayoutInflater.from(parent.context)
                        .inflate(R.layout.single_slide_layout, parent, false))

            }

            override fun getItemCount() = slideList.size

            override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
                holder.bind(slideList[position]["title"]!!, slideList[position]["date"]!!)

                holder.download.setOnClickListener {
                    val fileUrl = slideList[position]["link"] ?: return@setOnClickListener
                    openWebPage(fileUrl)
                }
            }
        }

        if (slide_list != null && slideList.size == 0) {
            slide_empty.visibility = View.VISIBLE
            slide_list.visibility = View.GONE
        } else {
            slide_empty.visibility = View.GONE
            slide_list.visibility = View.VISIBLE
            slide_list.adapter = slideAdapter
        }
    }

    override fun showSlideUploadButton(teacher: Boolean) {
        if (teacher) {
            slide_upload_button.show()
            slide_upload_button.setOnClickListener {
                startActivityForResult(Intent.createChooser(IntentResult.forPDF(), "Select Document"), 0)
            }
        } else {
            slide_upload_button.hide()
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null && data.data != null) {
            val uri = data.data ?: return
            val uploadingIntent = Intent(this, MyUploadingService::class.java)
            val currentTime = System.currentTimeMillis().toString()
            FileBuilderNew.upload(uri, this@SlideActivity , uploadingIntent, "Slide/$classId/$currentTime")
        } else {
            showMessage("PDF can't be retrieve.");
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}


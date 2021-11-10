package com.btp.me.classroom.slide

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.india.engaze.R
import com.india.engaze.screens.HomePage.HomeContract
import com.india.engaze.screens.HomePage.MainActivity.Companion.classId
import com.india.engaze.screens.Splash.SplashActivity
import com.india.engaze.screens.adapter.SlideViewHolder
import com.india.engaze.screens.base.BaseActivity
import com.india.engaze.screens.slide.MyUploadingService
import com.india.engaze.screens.slide.SlideContract
import com.india.engaze.utils.IntentResult
import com.india.engaze.utils.TimberLogger
import kotlinx.android.synthetic.main.activity_slide.*
import kotlinx.android.synthetic.main.single_slide_layout.view.*
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
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

    override fun showSlides(slideList : ArrayList<HashMap<String, String>>){
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

    fun openWebPage(url: String?) {
        val webpage = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == 0 && data != null && data.data != null) {
            val uri = data.data ?: return
            upload(uri)
        } else {
            showMessage("PDF can't be retrieve.");
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun upload(uri: Uri) {
        var file = uri.lastPathSegment
        if (!file?.endsWith(".pdf")!!) {
            file += ".pdf"
        }
        val data = """{"title": "$file","link": ""}"""
        val uploadingIntent = Intent(this, MyUploadingService::class.java)
        val currentTime = System.currentTimeMillis().toString()
        uploadingIntent.putExtra("fileUri", uri)
        uploadingIntent.putExtra("storagePath", "Slide/$classId/$currentTime")
        uploadingIntent.putExtra("databasePath", "Slide/$classId/$currentTime")
        uploadingIntent.putExtra("data", data)
        uploadingIntent.action = MyUploadingService.ACTION_UPLOAD
        startService(uploadingIntent)
                ?: Timber.e("At this this no activity is running")
    }

}


package com.india.engaze.screens.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.india.engaze.AppController
import com.india.engaze.R
import com.india.engaze.screens.CreateClass.CreateClassActivity
import com.india.engaze.screens.JoinClass.JoinClass
import com.india.engaze.screens.PendingClassRequest.PendingRequestActivity
import com.india.engaze.screens.Splash.SplashActivity
import com.india.engaze.screens.assignment.AssignmentActivity
import com.india.engaze.screens.slide.SlideActivity
import com.india.engaze.screens.student.StudentMarksActivity
import kotlinx.android.synthetic.main.bottom_navigation_drawer_class.view.*


class ClassMenuFragment : BottomSheetDialogFragment() {
    //Bottom Sheet Callback
    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)


        //Get the content View
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_navigation_drawer_class, null)
        dialog.setContentView(view)
        with(view) {
            settings.setOnClickListener { goToSettings() }
            studyMaterials.setOnClickListener { goToSlides() }
            assignments.setOnClickListener { goToAssignments() }

            marks.setOnClickListener { goToMarks() }
            examination.visibility = View.GONE
            pendingRequest.setOnClickListener {
                goToPendingRequest()
            }

            if(AppController.getInstance().getmSessionManager().isTeacher) {
                marks.visibility = View.GONE
            }else{
                pendingRequest.visibility = View.GONE
                settings.visibility = View.GONE
            }
            closeView.setOnClickListener { dismiss() }
        }
        //Set the coordinator layout behavior
        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        //Set callback
        if (behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
    }

    private fun goToPendingRequest() {
        dismiss()
        startActivity(Intent(context, PendingRequestActivity::class.java))
    }

    private fun goToMarks() {
        dismiss()
        startActivity(Intent(context, StudentMarksActivity::class.java))
    }


    private fun goToAssignments() {
        dismiss()
        startActivity(Intent(context, AssignmentActivity::class.java))
    }

    private fun goToSlides() {
        startActivity(Intent(context, SlideActivity::class.java))
    }

    private fun goToSettings() {
        dismiss()
        startActivity(Intent(context, CreateClassActivity::class.java))
    }


    companion object {
        private var teacher: Boolean = false;

        @JvmStatic
        fun newInstance(isTeacher: Boolean): ClassMenuFragment {
            teacher = isTeacher;
            val args = Bundle()
            val fragment = ClassMenuFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
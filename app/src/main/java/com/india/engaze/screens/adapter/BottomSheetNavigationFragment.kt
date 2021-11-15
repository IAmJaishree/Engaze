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
import com.india.engaze.screens.Authentication.BasicDetailsInput.UserProfileActivity
import com.india.engaze.screens.CreateClass.CreateClassActivity
import com.india.engaze.screens.JoinClass.JoinClass
import com.india.engaze.screens.Splash.SplashActivity
import kotlinx.android.synthetic.main.bottom_navigation_drawer.view.*

class BottomSheetNavigationFragment : BottomSheetDialogFragment() {
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
        val view = LayoutInflater.from(context).inflate(R.layout.bottom_navigation_drawer, null)
        dialog.setContentView(view)
        with(view) {
            user_name.text = AppController.getInstance().firebaseUser.displayName
//            user_email.text = "${AppController.getInstance().getmSessionManager().user.phone}"
            profileContainer.setOnClickListener { goToProfile() }
            logout.setOnClickListener { logout() }
            join_class.setOnClickListener { joinClass() }
            create_class.setOnClickListener { createClass() }
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

    private fun createClass() {
        dismiss()
        val intent = Intent(context, CreateClassActivity::class.java)
        startActivity(intent)
    }

    private fun logout() {
        dismiss()
        AppController.getInstance().logout()
        val intent = Intent(context, SplashActivity::class.java)
        startActivity(intent)
    }

    fun goToProfile() {
        dismiss()
        val intent = Intent(context, UserProfileActivity::class.java)
        startActivity(intent)

    }

    private fun joinClass() {
        dismiss()
        startActivity(Intent(context, JoinClass::class.java))
    }


    companion object {
        @JvmStatic
        fun newInstance(): BottomSheetNavigationFragment {
            val args = Bundle()
            val fragment = BottomSheetNavigationFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
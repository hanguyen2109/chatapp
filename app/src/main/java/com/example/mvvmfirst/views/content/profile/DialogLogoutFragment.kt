package com.example.mvvmfirst.views.content.profile

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmfirst.R
import com.example.mvvmfirst.views.content.FirstActivity
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_logout.*

class DialogLogoutFragment : DialogFragment() {
    private var friendViewModel: FriendViewModel? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_logout, container, false)
        friendViewModel = ViewModelProviders.of(requireActivity())
            .get<FriendViewModel>(FriendViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonDialogLogout.setOnClickListener { logout()}
        buttonDialogNo.setOnClickListener {
            dismiss()
        }
    }
    private fun logout() {
        friendViewModel?.updateStatusUser("status", "offline")
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(activity, FirstActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        requireActivity().finish()
    }
}

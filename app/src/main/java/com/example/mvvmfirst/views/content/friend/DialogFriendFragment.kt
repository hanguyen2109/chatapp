package com.example.mvvmfirst.views.content.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.mvvmfirst.R
import kotlinx.android.synthetic.main.dialog_fragment_friend.*
import java.util.*

class DialogFriendFragment: DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_friend, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonDialogFriendOk.setOnClickListener{
            dismiss()
        }
    }
}

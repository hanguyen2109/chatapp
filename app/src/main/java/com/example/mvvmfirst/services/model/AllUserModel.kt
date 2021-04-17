package com.example.mvvmfirst.services.model

import androidx.recyclerview.widget.DiffUtil

data class AllUserModel(
    var userID: String = "",
    var userName: String = "",
    var userType: String = "",
    var userImage: String = ""
) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<AllUserModel>() {
            override fun areItemsTheSame(oldItem: AllUserModel, newItem: AllUserModel) =
                oldItem.userID == newItem.userID

            override fun areContentsTheSame(oldItem: AllUserModel, newItem: AllUserModel) =
                oldItem == newItem
        }
    }
}


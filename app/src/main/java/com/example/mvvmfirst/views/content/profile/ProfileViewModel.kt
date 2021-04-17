package com.example.mvvmfirst.views.content.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmfirst.services.model.UserSignModel
import com.example.mvvmfirst.services.repositories.ProfileResponsitory

class ProfileViewModel: ViewModel() {
    var userMutableLiveData: MutableLiveData<UserSignModel> = MutableLiveData()

    fun getInfoUser() {
        ProfileResponsitory().infoUserFromDatabase(object : ProfileResponsitory.LoadUser{
            override fun loadUser(user: UserSignModel) {
                userMutableLiveData.value = user
            }
        })
    }
}

package com.example.mvvmfirst.views.content.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmfirst.services.model.UserSignModel
import com.example.mvvmfirst.services.repositories.ProfileResponsitory

class EditProfileViewModel: ViewModel() {
    var userMutableLiveData: MutableLiveData<UserSignModel> = MutableLiveData<UserSignModel>()
    fun getInfoUser() {
        ProfileResponsitory().infoUserFromDatabase(object : ProfileResponsitory.LoadUser{
            override fun loadUser(user: UserSignModel) {
                userMutableLiveData.value = user
            }
        })
    }

    fun updateInfoUser(key: String?, value: String?) {
        if (key != null) {
            if (value != null) {
                ProfileResponsitory().updateUser(key, value)
            }
        }
    }

    fun validatePhoneNumber(a: String): Boolean? {
        return a.length == 10 || a == "default"
    }
}

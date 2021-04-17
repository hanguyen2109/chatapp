package com.example.mvvmfirst.views.content.friend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmfirst.services.model.AllUserModel
import com.example.mvvmfirst.services.repositories.FriendResponsitory
import com.example.mvvmfirst.services.repositories.ProfileResponsitory

class FriendViewModel : ViewModel() {
    var arrayAllFriend: ArrayList<AllUserModel> = ArrayList()
    var countRequest = MutableLiveData("0")
    var allUserArray: MutableLiveData<ArrayList<AllUserModel>> = MutableLiveData()
    var getUserArray: MutableLiveData<ArrayList<AllUserModel>> = MutableLiveData()

    //get yc friend
     fun getUser() {
        FriendResponsitory().getUserInfo(object : FriendResponsitory.LoadInfoUser {
            override fun loadInfoUser(allUserModel: ArrayList<AllUserModel>, count: Int) {
                allUserArray.value = allUserModel
                getUserArray.value = allUserModel
                arrayAllFriend = allUserModel
                if (count > 10) {
                    countRequest.setValue("9+")
                } else countRequest.setValue(count.toString())
            }
        })
    }

    fun createFriend(userModelFriend: AllUserModel) {
        FriendResponsitory().createFriend(userModelFriend)
    }

    fun deleteFriend(userModelFriend: AllUserModel) {
        FriendResponsitory().deleteFriend(userModelFriend)
        FriendResponsitory().deleteFriendOfchat(userModelFriend.userID)
    }

    fun updateFriend(userModelFriend: AllUserModel) {
        FriendResponsitory().updateIsFriend(userModelFriend)
    }

    fun searchFriend(searchString: String, getUser: ArrayList<AllUserModel>) {
        val userArrayList = ArrayList<AllUserModel>()
        for (i in getUser.indices) {
            val name = getUser[i].userName
            if (name.toLowerCase().trim().contains(searchString.toLowerCase().trim())) {
                userArrayList.add(getUser[i])
            }
        }
        allUserArray.value = userArrayList
    }

    fun updateStatusUser(key: String, value: String) {
        ProfileResponsitory().updateUser(key, value)
    }
}

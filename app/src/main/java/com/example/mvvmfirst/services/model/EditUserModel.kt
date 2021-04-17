package com.example.mvvmfirst.services.model

class EditUserModel {
    private var userName: String? = null
    private var userPhone: String? = null
    private var userDateOfBirth: String? = null
    constructor(userName: String?, userPhone: String?, userDateOfBirth: String?){
        this.userName = userName
        this.userPhone = userPhone
        this.userDateOfBirth = userDateOfBirth
    }
    fun getUserName(): String?{
        return userName
    }
    fun setUserName(userName: String?){
        this.userName = userName
    }
    fun getUserPhone(): String?{
        return userPhone
    }
    fun setUserPhone(userPhone: String?){
        this.userPhone = userPhone
    }
    fun getUserDateOfBirth(): String?{
        return userDateOfBirth
    }
    fun setUserDateOfBirth(userDateOfBirth: String?){
        this.userDateOfBirth = userDateOfBirth
    }
}

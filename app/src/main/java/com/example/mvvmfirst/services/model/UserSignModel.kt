package com.example.mvvmfirst.services.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class UserSignModel {
    private var userId: String = ""
    private var userName: String = ""
    private var userEmail: String = ""
    private var userImage: String = ""
    private var userPhone: String = ""
    private var userDate: String = ""
    private var userStatus: String = ""

    constructor() {}

    constructor(
        userId: String,
        userName: String,
        userEmail: String,
        userImage: String,
        userPhone: String,
        userDate: String,
        userStatus: String
    ) {
        this.userId = userId
        this.userName = userName
        this.userEmail = userEmail
        this.userImage = userImage
        this.userPhone = userPhone
        this.userDate = userDate
        this.userStatus = userStatus
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId!!
    }

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName!!
    }

    fun getUserEmail(): String? {
        return userEmail
    }

    fun setUserEmail(userEmail: String?) {
        this.userEmail = userEmail!!
    }

    fun getUserImgUrl(): String? {
        return userImage
    }

    fun setUserImgUrl(userImage: String) {
        this.userImage = userImage
    }

    fun getUserPhone(): String? {
        return userPhone
    }

    fun setUserPhone(userPhone: String?) {
        this.userPhone = userPhone!!
    }

    fun getUserDateOfBirth(): String? {
        return userDate
    }

    fun setUserDateOfBirth(userDate: String) {
        this.userDate = userDate
    }

    fun setStatus(userStatus: String) {
        this.userStatus = userStatus
    }

    fun getStatus(): String? {
        return userStatus
    }
}

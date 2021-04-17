package com.example.mvvmfirst.views.content.sign

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmfirst.services.model.UserSignModel
import com.example.mvvmfirst.services.repositories.SignReponsitory
import com.google.firebase.auth.FirebaseAuth

class SignViewModel : ViewModel() {
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var signupSuccess = MutableLiveData<Boolean>()
    private var signupStatus = MutableLiveData<SignUpStatus>()

    fun createUserFirebase(name: String, email: String, password: String) {
        signupStatus.value = SignUpStatus.Loading(true)
        firebaseAuth.createUserWithEmailAndPassword(
            email, password
        ).addOnSuccessListener {
            val firebaseuser = firebaseAuth.currentUser
            val userId = firebaseuser!!.uid
            val user = UserSignModel(
                userId,
                name,
                email,
                "default",
                "default",
                "default",
                "offline"
            )
            SignReponsitory().createUserInFirebase(user, userId)
            signupSuccess.value = true
            signupStatus.value = SignUpStatus.IsSuccess(true)
            signupStatus.value = SignUpStatus.Loading(false)
        }
            .addOnFailureListener {
                signupStatus.value = SignUpStatus.Loading(false)
                signupStatus.value = SignUpStatus.IsSuccess(false)
                signupStatus.value = SignUpStatus.Failure(it)
            }
    }

    fun resetStatus() {
        signupStatus.value = SignUpStatus.Loading(false)
    }


    sealed class SignUpStatus {

        data class Loading(var loading: Boolean) : SignUpStatus()

        data class IsSuccess(var isLogin: Boolean) : SignUpStatus()

        data class Failure(var e: Throwable) : SignUpStatus()

        data class ErrorPassAndEmail(var isError: Boolean) : SignUpStatus()
    }

}
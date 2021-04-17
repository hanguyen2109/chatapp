package com.example.mvvmfirst.services.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

object LoginReponsitory {
    private var firebaseLogin =FirebaseAuth.getInstance()
    //check login
    fun login(email:String, password:String) :Task<AuthResult>{
        return firebaseLogin.signInWithEmailAndPassword(email, password)
    }
}

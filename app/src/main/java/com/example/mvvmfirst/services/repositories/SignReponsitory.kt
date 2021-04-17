package com.example.mvvmfirst.services.repositories
import com.example.mvvmfirst.services.model.UserSignModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignReponsitory {
    var databaseReference: DatabaseReference? = null

    fun createUserInFirebase(user: UserSignModel?, userId: String?) {
        databaseReference = FirebaseDatabase.getInstance().getReference("user")
        databaseReference!!.child(userId!!).setValue(user)
    }
}

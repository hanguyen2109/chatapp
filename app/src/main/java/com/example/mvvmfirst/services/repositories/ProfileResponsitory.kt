package com.example.mvvmfirst.services.repositories

import com.example.mvvmfirst.services.model.UserSignModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileResponsitory {
    private var databaseReference: DatabaseReference? = null
    private var firebaseUser = FirebaseAuth.getInstance().currentUser
    var user = UserSignModel()
    fun infoUserFromDatabase(loadUser: LoadUser){
        if(firebaseUser==null){
            return
        }
        val myId = firebaseUser!!.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(myId)
        databaseReference!!.addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                user = snapshot.getValue(UserSignModel::class.java)!!
                loadUser.loadUser(user)
            }

        })
    }
    fun updateUser(key: String, value: String){
        val myID = firebaseUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("user")
        if (myID != null) {
            databaseReference!!.child(myID).child(key).setValue(value)
        }
    }
    interface  LoadUser{
        fun loadUser(user: UserSignModel)
    }
}


package com.example.mvvmfirst.services.repositories

import androidx.lifecycle.MutableLiveData
import com.example.mvvmfirst.services.model.AllUserModel
import com.example.mvvmfirst.services.model.FriendModel
import com.example.mvvmfirst.services.model.UserSignModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.IOException

class FriendResponsitory {
    private var databaseReference: DatabaseReference? = null
    var firebaseUser = FirebaseAuth.getInstance().currentUser
    var userArrayList: ArrayList<UserSignModel> = ArrayList()//list user
    var friendArrayList: ArrayList<FriendModel> = ArrayList()//list friend
    var allUserArrayList: ArrayList<AllUserModel> = ArrayList()//list all user (friend + unfriend)
    var countRequest: Int = 0
    var isLoadUserOk = MutableLiveData(false)
    var isLoadfriendOk = MutableLiveData(false)

    //add friend
    fun createFriend(user: AllUserModel) {
        databaseReference = FirebaseDatabase.getInstance().getReference("friend")
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            return
        }
        var myId = firebaseUser.uid
        var friendSendForYou = FriendModel(user.userID, "sendRequest")
        var youSendForFriend = FriendModel(myId, "friendRequest")
        databaseReference?.child(myId)?.child(user.userID)
            ?.setValue(friendSendForYou)//add vao friend of you
        databaseReference?.child(user.userID)?.child(myId)
            ?.setValue(youSendForFriend)//add vao friend cua nguoi khac
    }

    //delete friend
    fun deleteFriend(user: AllUserModel) {
        databaseReference = FirebaseDatabase.getInstance().getReference("friend")
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            return
        }
        val myID = firebaseUser.uid
        databaseReference?.child(myID)?.child(user.userID)?.setValue(null)
        databaseReference?.child(user.userID)?.child(myID)?.setValue(null)

    }
    fun deleteFriendOfchat(idFriend: String){
        val firebaseUser = FirebaseAuth.getInstance().currentUser ?: return
        val myID = firebaseUser.uid
        val key = if (idFriend > myID) {
            idFriend + myID
        } else {
            myID + idFriend
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("chat").child(key)
        databaseReference!!.setValue(null)
    }

    //update friend when you accept
    fun updateIsFriend(user: AllUserModel) {
        databaseReference = FirebaseDatabase.getInstance().getReference("friend")
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) return
        var myID = firebaseUser.uid
        databaseReference?.child(myID)?.child(user.userID)?.child("type")?.setValue("friend")
        databaseReference?.child(user.userID)?.child(myID)?.child("type")?.setValue("friend")
    }

    //get user trừ mk
    fun getUser(dataStatus: LoadUserAndFriend) {
        databaseReference = FirebaseDatabase.getInstance().getReference("user")
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userArrayList.clear()
                for (Node in dataSnapshot.children) {
                    val userModel: UserSignModel? = Node.getValue(UserSignModel::class.java)
                    if (userModel != null) {
                        if (!userModel.getUserId().equals(firebaseUser?.uid)) {
                            userArrayList.add(userModel)
                        }
                    }
                }
                isLoadUserOk.value = true
                dataStatus.loadUser(userArrayList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //get friend of my
    fun getFriend(dataStatus: LoadUserAndFriend) {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("friend").child(firebaseUser!!.uid)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friendArrayList.clear()
                for (Node in dataSnapshot.children) {
                    val friendModel: FriendModel? = Node.getValue(FriendModel::class.java)
                    if (friendModel != null) {
                        friendArrayList.add(friendModel)
                    }
                }
                isLoadfriendOk.value = true
                dataStatus.loadFriend(friendArrayList)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //get info of user
    fun getUserInfo(infoUser: LoadInfoUser) {
        getUser(object : LoadUserAndFriend {
            override fun loadUser(userModels: ArrayList<UserSignModel>) {
                userArrayList = userModels
                if (isLoadUserOk.value != null && isLoadUserOk.value!!) {
                    getFriend(object : LoadUserAndFriend {
                        override fun loadUser(userModels: ArrayList<UserSignModel>) {}
                        override fun loadFriend(friendsModels: ArrayList<FriendModel>) {
                            friendArrayList = friendsModels
//                            if (isLoadfriendOk.getValue() != null && isLoadfriendOk.getValue()!!) {
                            allUserArrayList.clear()
                            countRequest = 0
                            for (i in userArrayList.indices) {
                                val allUserModel = AllUserModel()
                                allUserModel.userID = userArrayList[i].getUserId().toString()
                                allUserModel.userImage = userArrayList[i].getUserImgUrl().toString()
                                allUserModel.userName = userArrayList[i].getUserName().toString()
                                var sum = 0
                                for (j in friendArrayList.indices) {
                                    if (userArrayList[i].getUserId() == (friendArrayList[j].getIdFriend())
                                    ) {
                                        sum++
                                        allUserModel.userType =
                                            friendArrayList[j].getType().toString()
                                        if (friendArrayList[j].getType() == "friendRequest") countRequest++
                                    }
                                }
                                if (sum == 0) {
                                    allUserModel.userType = "NoFriend"
                                }
                                allUserArrayList.add(allUserModel)
                            }
                            infoUser.loadInfoUser(allUserArrayList, countRequest)
//                            }
                        }
                    })
                }
            }

            override fun loadFriend(friendsModels: java.util.ArrayList<FriendModel>) {}
        })
    }

    interface LoadUserAndFriend {
        fun loadUser(userSignModel: ArrayList<UserSignModel>)
        fun loadFriend(friendModel: ArrayList<FriendModel>)
    }

    interface LoadInfoUser {
        fun loadInfoUser(allUserModel: ArrayList<AllUserModel>, count: Int)
    }
}

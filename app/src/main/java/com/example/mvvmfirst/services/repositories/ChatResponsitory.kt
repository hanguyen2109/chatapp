package com.example.mvvmfirst.services.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mvvmfirst.BuildConfig
import com.example.mvvmfirst.services.model.ChatModel
import com.example.mvvmfirst.services.model.MessageModel
import com.example.mvvmfirst.services.model.UserSignModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatResponsitory {
    var databaseReference: DatabaseReference? = null
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser: FirebaseUser? = firebaseAuth.currentUser
    var listIdChat = ArrayList<String>()
    var arrayAllUserChat = ArrayList<UserSignModel>()
    var isOk = MutableLiveData(false)
    var isLoadInfoUser = MutableLiveData(false)
    var isLoadedMessage = MutableLiveData(false)

    //lay dl cua user tu firebase
    fun infoUserFromFirebase(id: String?, dataStatus: DataStatus): Unit {
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(id!!)
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val account: UserSignModel? = dataSnapshot.getValue(UserSignModel::class.java)
                if (account != null) {
                    dataStatus.isLoadedData(account)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    //create message
    fun createMessage(id: String, message: String, type: String) {
        val calendar: Calendar = Calendar.getInstance()
        val simpleDateFormatDate = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
        val simpleDateFormatTime = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date: String = simpleDateFormatDate.format(calendar.time)
        val hour: String = simpleDateFormatTime.format(calendar.time)
        databaseReference = FirebaseDatabase.getInstance().getReference("chat")
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val idUser = firebaseUser.uid
            val messageModel = MessageModel(
                idUser,//id nguoi gui
                id,//id nguoi nhan
                message,
                type,
                date,
                hour,
                false,
                System.currentTimeMillis(),
                false
            )
            var key: String = ""
            key = if (id > idUser) {
                id + idUser
            } else {
                idUser + id
            }
            databaseReference!!.child(key).push().setValue(messageModel)
        }
    }

    //lay 1 vai tin nhan cua id key (1 list tin nhan gom 9 cai)
    fun getSomeMessage(idFriend: String, messStatus: MessageStatus) {
        if (firebaseUser == null)
            return
        val myID = firebaseUser!!.uid
        var key = if (idFriend > myID) {
            idFriend + myID
        } else {
            myID + idFriend
        }
        val query = FirebaseDatabase.getInstance().getReference("chat").child(key).limitToLast(9)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                val messList = ArrayList<MessageModel>()
                messList.clear()
                if(snapshot.exists()){
                    for (keyOne in snapshot.children) {
                        val message = keyOne.getValue(MessageModel::class.java)
                        if (message != null) {
                            messList.add(message)
                        }
                    }
                    isLoadedMessage.value = true
                    messStatus.isLoadedData(messList)
                }

            }
        })
    }

    //get message
    fun getMessage(idFriend: String, lastPositionChat: Long, messStatus: MessageStatus) {
        if (firebaseUser == null)
            return
        val myID = firebaseUser!!.uid
        val key = if (idFriend.compareTo(myID) > 0) {
            idFriend + myID
        } else {
            myID + idFriend
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("chat").child(key)
        if (lastPositionChat == 0L) {
            databaseReference?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    var messageList = ArrayList<MessageModel>()
                    messageList.clear()
                    if (snapshot.exists())
                    {
                        for (keyOne in snapshot.children) {
                            var message = keyOne.getValue(MessageModel::class.java)
                            if (message != null) {
                                messageList.add(message)//list 15 tin nhan
                            }
                        }
                        messStatus.isLoadedData(messageList)
                    }

                }
            })
        } else {
            Log.d("Last time respotion", "" + lastPositionChat)
            databaseReference!!.orderByChild("timeLong").endAt(lastPositionChat.toDouble())
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {}
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messList: ArrayList<MessageModel> = ArrayList()
//                        messList.clear()
                        if (snapshot.exists())
                        {
                            for (keyOne in snapshot.children) {
                                val message = keyOne.getValue(MessageModel::class.java)
                                if (message != null) {
                                    messList.add(message)//1 list 15 cai moi nhat
                                }
                            }
                            messStatus.isLoadedData(messList)
                        }

                    }
                })
        }
    }

        // lay cac id cua cac user nam trong bang chat tru myID
        fun getAllIdListChat(listIdChatStatus: ListIdChatStatus) {
            databaseReference = FirebaseDatabase.getInstance().getReference("chat")
            databaseReference!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    listIdChat.clear()
                    for (keyOne in snapshot.children) {
                        var key = keyOne.key
                        if (BuildConfig.DEBUG && key == null) {
                            error("Assertion failed")
                        }
                        if (key != null) {
                            if (key.contains(firebaseUser?.uid!!)) {
                                val id1 = key.substring(0, key.length / 2)
                                val id2 = key.substring(key.length / 2)
                                if (id1 != firebaseUser!!.uid) {
                                    listIdChat.add(id1)
                                } else {
                                    listIdChat.add(id2)
                                }
                            }
                        }
                    }
                    isOk.value = true
                    listIdChatStatus.isLoadedData(listIdChat)
                }
            })
        }

        //ds info cua cac user chat
        fun getAllInfoUserChat(listInfoAllUser: ListInfoAllUserChat) {
            getAllIdListChat(object : ListIdChatStatus {
                override fun isLoadedData(listKey: ArrayList<String>) {
                    if (isOk.value != null && isOk.value!!) {
                        databaseReference = FirebaseDatabase.getInstance().getReference("user")
                        databaseReference!!.addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {}

                            override fun onDataChange(snapshot: DataSnapshot) {
                                arrayAllUserChat.clear()
                                for (keyOne in snapshot.children) {
                                    var user: UserSignModel? =
                                        keyOne.getValue(UserSignModel::class.java)
                                    for (i in listKey.indices) {
                                        if (BuildConfig.DEBUG && user == null) {
                                            error("Assertion failed")
                                        }
                                        if (user != null) {
                                            if (listKey[i] == user.getUserId()) {
                                                arrayAllUserChat.add(user)
                                            }
                                        }
                                    }
                                }
                                isLoadInfoUser.value = true
                                listInfoAllUser.isLoadedData(arrayAllUserChat)
                            }
                        })
                    }
                }
            })
        }

        //check seen
        fun checkSeen(idFriend: String): DatabaseReference? {
            val myID = firebaseUser!!.uid
            val key: String
            key = if (idFriend > myID) {
                idFriend + myID
            } else {
                myID + idFriend
            }
            return FirebaseDatabase.getInstance().getReference("chat").child(key)
        }

        //get list chatmodel(message + user) of all chat
        fun getListMessage(listMessStatus: ListMessStatus) {
            getAllInfoUserChat(object : ListInfoAllUserChat {
                override fun isLoadedData(arrayInfoAllUser: ArrayList<UserSignModel>) {
                    if (isLoadInfoUser.value != null && isLoadInfoUser.value!!) {
                        val listChat = ArrayList<ChatModel>()//list chatModel(user+ message)
                        listChat.clear()
                        for (i in arrayInfoAllUser.indices) {
                            val chatModel = ChatModel()
                            getSomeMessage(
                                arrayInfoAllUser[i].getUserId()!!,
                                object : MessageStatus {
                                    override fun isLoadedData(listKey: ArrayList<MessageModel>) {
                                        if (isLoadedMessage.value != null && isLoadedMessage.value!!) {
                                            listChat.remove(chatModel)
                                            chatModel.setUserModel(arrayInfoAllUser[i])
                                            chatModel.setMessageModelArrayList(listKey)
                                            isLoadedMessage.value = false
                                        }
                                        listChat.add(chatModel)
                                        if (i == arrayInfoAllUser.size - 1) {
                                            listMessStatus.isLoadedData(listChat)
                                        }
                                    }
                                })
                        }
                    }
                }
            })
        }

        //get chatModel
        interface ListMessStatus {
            fun isLoadedData(arrayChat: ArrayList<ChatModel>)
        }

        //lay user
        interface DataStatus {
            fun isLoadedData(user: UserSignModel)
        }

        //lay message
        interface MessageStatus {
            fun isLoadedData(listKey: ArrayList<MessageModel>)
        }

        //lay id all user chat
        interface ListIdChatStatus {
            fun isLoadedData(listKey: ArrayList<String>);
        }

        //lay info all user chat
        interface ListInfoAllUserChat {
            fun isLoadedData(arrayInfoAllUser: ArrayList<UserSignModel>)
        }

    }

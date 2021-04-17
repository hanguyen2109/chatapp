package com.example.mvvmfirst.views.content.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmfirst.BuildConfig
import com.example.mvvmfirst.services.model.ChatModel
import com.example.mvvmfirst.services.model.MessageModel
import com.example.mvvmfirst.services.model.UserSignModel
import com.example.mvvmfirst.services.repositories.ChatResponsitory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.util.*
import kotlin.collections.ArrayList

class ChatViewModel : ViewModel() {
    var firebaseUser = FirebaseAuth.getInstance().currentUser//user login now
    var messageList = MutableLiveData<ArrayList<MessageModel>>()//list mess of 1 user
    var userInfoUserChat = MutableLiveData<ArrayList<ChatModel>>()//list info(name, mess) user chat
    var arraySearchUser = MutableLiveData<ArrayList<ChatModel>>()
    var userChatLiveData: MutableLiveData<UserSignModel> = MutableLiveData<UserSignModel>()
    var isShowProcessLoadMessage = MutableLiveData(false)
    var countNotRead = MutableLiveData<String>()
    var chatResponsitory: ChatResponsitory = ChatResponsitory()
    fun getAll() {
//        chatResponsitory = ChatResponsitory()
        chatResponsitory.getListMessage(object : ChatResponsitory.ListMessStatus {
            override fun isLoadedData(arrayChat: ArrayList<ChatModel>) {
                userInfoUserChat.value = arrayChat
                arraySearchUser.value = arrayChat
            }
        })
    }

    //get info user chat id, { user -> userChatLiveData.setValue(user) }
    fun getInfoUserChat(id: String?) {
        if (id != null) {
            chatResponsitory.infoUserFromFirebase(id, object : ChatResponsitory.DataStatus {
                override fun isLoadedData(user: UserSignModel) {
                    userChatLiveData.value = user
                }

            })
        }
    }


    //send message
    fun sendMessage(idUser: String, message: String, type: String) {
        chatResponsitory.createMessage(idUser, message, type)
    }

    //show message
    fun showMessageLast(idFriend: String, lastPositionMess: Long) {
        isShowProcessLoadMessage.value = true
        chatResponsitory.getMessage(
            idFriend,
            lastPositionMess,
            object : ChatResponsitory.MessageStatus {
                override fun isLoadedData(listKey: ArrayList<MessageModel>) {
                    var listMess: ArrayList<MessageModel> = ArrayList()
                    if (lastPositionMess == 0L) {
                        messageList.value = listKey
                        listMess = listKey
                    } else {
                        val newList = ArrayList<MessageModel>()
                        val oldList: ArrayList<MessageModel> = listMess
                        if (BuildConfig.DEBUG && oldList == null) {
                            error("Assertion failed")
                        }
                        if (listMess.size > 0 && oldList.size > 0) {
                            if (oldList[0].getTimeLong() != listKey[0].getTimeLong()) {
                                newList.addAll(listKey)
                            }
                            newList.addAll(oldList)
                            messageList.value = newList
                        }
                        isShowProcessLoadMessage.value = false
                    }
                }

            })
    }

    //check seen message
    fun checkSeen(idFriend: String?): DatabaseReference? {
        return chatResponsitory.checkSeen(idFriend!!)
    }

    //get sum not read message
    fun getCountNotReadMessage(chatModelArraylist: ArrayList<ChatModel>): Int {
        var count = 0
        val size = chatModelArraylist.size
        for (i in 0 until size - 1) {
            val size1: Int = chatModelArraylist[i].getMessageModelArrayList()!!.size
            if (!(chatModelArraylist[i].getMessageModelArrayList()?.get(size1 - 1)
                    ?.getCheckSeen()!!)
                && chatModelArraylist[i].getMessageModelArrayList()?.get(size1 - 1)
                    ?.getIdReceiver()!! == firebaseUser?.uid
            ) {
                count++
            }
        }
        return count
    }

    fun countSumAcountNotCheckSeen(chatModelArraylist: ArrayList<ChatModel>): Int {
        var count = 0
        val size = chatModelArraylist.size
        for (i in 0 until size - 1) {
            val size1: Int = chatModelArraylist[i].getMessageModelArrayList()!!.size
            if (!(chatModelArraylist[i].getMessageModelArrayList()?.get(size1 - 1)
                    ?.getCheckSeen()!!)
                && chatModelArraylist[i].getMessageModelArrayList()?.get(size1 - 1)
                    ?.getIdReceiver()!! == firebaseUser?.uid
            ) {
                count++
                return 0
            }
        }
        return count
    }

    //search user chat
    fun searchUserChat(searchString: String, getUser: ArrayList<ChatModel>) {
        val allUserList = ArrayList<ChatModel>()
        for (i in getUser.indices) {
            val name = getUser[i].getUserModel()!!.getUserName()
            if (name!!.toLowerCase().contains(searchString.toLowerCase())) {
                allUserList.add(getUser[i])
            }
        }
        userInfoUserChat.value = allUserList
    }

    fun sortArray(chatModelArray: ArrayList<ChatModel>) {
        val size = chatModelArray.size
        for (i in 0 until size - 1) {
            for (j in 0 until size - i - 1) {
                val size1 = chatModelArray[j].getMessageModelArrayList()!!.size - 1
                val size2 = chatModelArray[j + 1].getMessageModelArrayList()!!.size - 1
                if (chatModelArray[j].getMessageModelArrayList()!![size1]!!.getTimeLong() <
                    chatModelArray[j + 1].getMessageModelArrayList()!![size2]!!.getTimeLong()
                ) {
                    Collections.swap(chatModelArray, j, j + 1)
                }
            }
        }
    }
}

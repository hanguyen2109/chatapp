package com.example.mvvmfirst.services.model

import androidx.recyclerview.widget.DiffUtil
import java.util.*

class ChatModel {
    private var userSignModel: UserSignModel? = null
    private var messageModelArrayList: List<MessageModel>? = null
    constructor(){}

    constructor(
        userSignModel: UserSignModel?,
        messageModelArrayList: ArrayList<MessageModel>?
    ) {
        this.userSignModel = userSignModel
        this.messageModelArrayList = messageModelArrayList
    }

    fun getUserModel(): UserSignModel? {
        return userSignModel
    }

    fun setUserModel(userSignModel: UserSignModel) {
        this.userSignModel = userSignModel
    }

    fun getMessageModelArrayList(): List<MessageModel?>? {
        return messageModelArrayList
    }

    fun setMessageModelArrayList(messageModelArrayList: List<MessageModel?>?) {
        this.messageModelArrayList = messageModelArrayList as List<MessageModel>?
    }

}

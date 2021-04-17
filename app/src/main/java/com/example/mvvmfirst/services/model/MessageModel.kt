package com.example.mvvmfirst.services.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class MessageModel {
    private var idSender: String = ""
    private var idReceiver: String = ""
    private var message: String = ""
    private var type: String = ""
    private var date: String = ""
    private var time: String = ""
    private var timeLong: Long = 0
    private var checkSeen: Boolean = false
    private var isShow: Boolean = false
    constructor(){}
    constructor(
        idSender: String?,
        idReceiver: String?,
        message: String?,
        type: String?,
        date: String?,
        time: String?,
        checkSeen: Boolean?,
        timeLong: Long,
        isShow: Boolean?
    ) {
        this.idSender = idSender!!
        this.idReceiver = idReceiver!!
        this.message = message!!
        this.type = type!!
        this.date = date!!
        this.time = time!!
        this.timeLong = timeLong
        this.checkSeen = checkSeen!!
        this.isShow = isShow!!
    }
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<MessageModel>() {
            override fun areItemsTheSame(oldItem: MessageModel, newItem: MessageModel) =
                oldItem.getTimeLong() == newItem.getTimeLong()

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: MessageModel, newItem: MessageModel) =
                oldItem == newItem
        }
    }
    fun getIsShow(): Boolean? {
        return isShow
    }

    fun setIsShow(isShow: Boolean?) {
        this.isShow = isShow!!
    }

    fun getCheckSeen(): Boolean? {
        return checkSeen
    }

    fun setCheckSeen(checkSeen: Boolean?) {
        this.checkSeen = checkSeen!!
    }

    fun getIdSender(): String? {
        return idSender
    }

    fun setIdSender(idSender: String?) {
        this.idSender = idSender!!
    }

    fun getIdReceiver(): String? {
        return idReceiver
    }

    fun setIdReceiver(idReceiver: String?) {
        this.idReceiver = idReceiver!!
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message!!
    }

    fun getType(): String? {
        return type
    }

    fun setType(type: String?) {
        this.type = type!!
    }

    fun getDate(): String? {
        return date
    }

    fun setDate(date: String?) {
        this.date = date!!
    }

    fun getTime(): String? {
        return time
    }

    fun setTime(time: String?) {
        this.time = time!!
    }

    fun getTimeLong(): Long {
        return timeLong
    }

    fun setTimeLong(timeLong: Long) {
        this.timeLong = timeLong
    }
}

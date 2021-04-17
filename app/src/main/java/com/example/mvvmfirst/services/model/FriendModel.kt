package com.example.mvvmfirst.services.model

class FriendModel {
    private var idFriend: String = ""
    private var type: String = ""//friend or request
    constructor(){}
    constructor(idFriend: String, type: String){
        this.idFriend = idFriend
        this.type = type
    }
    fun getIdFriend(): String {
        return idFriend
    }
    fun setIdFriend(idFriend: String){
        this.idFriend = idFriend
    }
    fun getType(): String{
        return type
    }
    fun setType(type: String){
        this.type = type
    }
}

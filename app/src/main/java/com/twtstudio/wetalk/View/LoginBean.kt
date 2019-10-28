package com.twtstudio.wetalk.View

class LoginBean(var msg: String, var code: String, var type: String, var token: String)

class LogoutBean(var code: String, var msg: String, var type: String)

class RegisBean(var msg: String, var code: String, var type: String)

class FriendsBean(var code: String, var msg: String, var type: String, var friends: List<String>)

class makeFriendsBean(var code: String, var msg: String, var type: String)

class sendMessageBean(var msg: String, var code: String, var type: String)

class receiveMessageBean(var msg: String, var code: String, var from: String, var time: String, var type: String)

class receiveFileBean(var msg: String,var code: String, var from: String, var time: String, var type: String, var filename: String)

class receiveMakeBean(var msg: String, var code: String, var from: String, var type: String)

class receiveResBean(var code: String, var msg: String, var type: String)

class itemBean(var text: String, var time: String)

class showBean(var text: String, var time: String, var loc:Int)

class ReadBean(var name:String){
    val messages = arrayListOf<showBean>()
}



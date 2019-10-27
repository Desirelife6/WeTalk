package com.twtstudio.wetalk.View

class LoginBean(var msg: String, var code: String, var type: String, var token: String)

class LogoutBean(var code: String, var msg: String, var type: String)

class RegisBean(var msg: String, var code: String, var type: String)
// {  “code”:0,  “msg”:”msg”,  “type”:” friends”,  “friends”:[“friend1”,”friend2”] }
class FriendsBean(var code: String, var msg: String, var type: String, var friends: List<String>)

class makeFriendsBean(var code: String, var msg: String, var type: String)

class sendMessageBean(var code: String, var msg: String, var type: String)

class receiveMessageBean(var msg: String, var code: String,var from: String,var time: String,var type: String)

class receiveMakeBean(var msg: String, var code: String,var from: String,var type: String)

class receiveResBean(var code: String, var msg: String, var type: String)

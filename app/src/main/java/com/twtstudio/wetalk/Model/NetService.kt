package com.twtstudio.wetalk.Model

import android.app.Activity
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.twtstudio.wetalk.View.*
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch
import org.jetbrains.anko.startActivity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket

const val host = "192.168.43.39"
val listenSocket = Socket(host, 12333)
var lastSendTime: Long = 0
val mainSocket = Socket(host, 12000)
val messageSocket = Socket(host, 13000)

object NetService {
    lateinit var os: OutputStream
    lateinit var ins: InputStream

    fun loginService(name: String, pwd: String, act: LoginActivity) {
        var message: String
        var temp: String
        launch {
            //val mainSocket = Socket(host, 12000)
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val loginMessage = "POST /login HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"pwd\":\"$pwd\"}\n^-^\n"
            os.write(loginMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            Log.d("HHHH", "发出请求")
            lateinit var bean: LoginBean
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            bean = gson.fromJson(temp, LoginBean::class.java)

            Log.d("HHHH", bean.msg)
            if (bean.code == "0" || bean.code == "2") {
                Hawk.put("token", bean.token)
                Hawk.put("userID", name)
                act.startActivity<MainActivity>()
                Hawk.put("Status", com.twtstudio.wetalk.View.LOGIN)
                act.finish()
            } else {
                launch(UI) {
                    act.showPopWindow(bean.msg)
                }
            }
            //mainSocket.close()
        }
    }

    fun logoutService(name: String, token: String, act: Activity) {
        var message: String
        var temp: String
        launch {
            //val mainSocket = Socket(host,12000)
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val logoutMessage = "POST /logout HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"token\":\"$token\"}\n^-^\n"

            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            lateinit var bean: LogoutBean

            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            bean = gson.fromJson(temp, LogoutBean::class.java)
            //mainSocket.close()
        }

    }

    fun regiService(name: String, pwd: String, act: LoginActivity) {
        var message: String
        var temp: String
        launch {
            //val mainSocket = Socket(host, 12000)
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val logoutMessage = "POST /register HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"pwd\":\"$pwd\"}\n^-^\n"

            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, RegisBean::class.java)
            if (bean.code == "0") {
                Hawk.put("userID", name)
                loginService(name, pwd, act)
            } else {
                launch(UI) {
                    act.showPopWindow(bean.msg)
                }
            }
            //mainSocket.close()
        }
    }

    fun friendsService(name: String, pwd: String, act: MainActivity) {
        var message: String
        var temp: String
        launch {
            //val mainSocket = Socket(host, 12000)
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val logoutMessage = "POST /friends HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"token\":\"$pwd\"}\n^-^\n"

            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, FriendsBean::class.java)
            launch(UI) {
                act.mTabList[1].setFriendList(bean.friends)
            }
            //mainSocket.close()
        }
    }

    fun deleteService(name: String, token: String, to: String,act: Context) {
        var message: String
        var temp: String
        launch {
            //val mainSocket = Socket(host, 12000)
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val logoutMessage = "POST /delete HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"token\":\"$token\",  \"newfriend\":\"$to\"}\n^-^\n"

            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, makeFriendsBean::class.java)
            launch(UI) {
                Toast.makeText(act,bean.msg,Toast.LENGTH_LONG).show()
            }

            //mainSocket.close()
        }
    }

    fun makeFriendsService(name: String, token: String, newfriend: String, act: MainActivity) {
        var message: String
        var temp: String
        launch {
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val logoutMessage = "POST /makefriends HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"token\":\"$token\",  \"newfriend\":\"$newfriend\"}\n^-^\n"
            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, makeFriendsBean::class.java)
            launch(UI) {
                Toast.makeText(act, bean.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun sendMessageService(
        name: String,
        token: String,
        newfriend: String,
        msg: String,
        act: TalkActivity
    ) {
        var message: String
        var temp: String
        launch {
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val logoutMessage = "POST /sendmsg HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"token\":\"$token\",  \"to\":\"$newfriend\",  \"msg\":\"$msg\",  \"time\":\"$hour:$minute\"}\n^-^\n"
            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, sendMessageBean::class.java)
            launch(UI) {
                if (bean.code == "0") {
                    act.itemAdapter.addItem(msg, com.twtstudio.wetalk.View.RIGHT)
                    act.recyclerView.smoothScrollToPosition(act.i)//移动到指定位置
                    act.i++
                } else
                    Toast.makeText(act, bean.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun confirmRequestService(
        name: String,
        token: String,
        newfriend: String,
        act: MainActivity
    ) {
        var message: String
        var temp: String
        launch {
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val logoutMessage = "POST /result HTTP/1.1\n^-^\n" +
                    "{\"from\":\"$name\",  \"to\":\"$newfriend\",  \"token\":\"$token\", \"status\":\"1\"}\n^-^\n"
            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, receiveResBean::class.java)
            launch(UI) {
                Toast.makeText(act, bean.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun confirmRequestService2(
        name: String,
        token: String,
        newfriend: String,
        act: TalkActivity
    ) {
        var message: String
        var temp: String
        launch {
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val logoutMessage = "POST /result HTTP/1.1\n^-^\n" +
                    "{\"from\":\"$name\",  \"to\":\"$newfriend\",  \"token\":\"$token\", \"status\":\"1\"}\n^-^\n"
            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, receiveResBean::class.java)
            launch(UI) {
                Toast.makeText(act, bean.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun sendPictureService(
        name: String,
        token: String,
        newfriend: String,
        msg: String,
        act: TalkActivity
    ) {
        var message: String
        var temp: String
        launch {
            os = mainSocket.getOutputStream()
            ins = mainSocket.getInputStream()
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val logoutMessage = "POST /pictures HTTP/1.1\n^-^\n" +
                    "{\"username\":\"$name\",  \"token\":\"$token\",  \"to\":\"$newfriend\",  \"msg\":\"$msg\",  \"time\":\"$hour:$minute\"}\n^-^\n"
            os.write(logoutMessage.toByteArray())
            lastSendTime = System.currentTimeMillis()
            val reader = BufferedReader(InputStreamReader(ins))
            temp = reader.readLine()
            val gson = Gson()
            val bean = gson.fromJson(temp, sendMessageBean::class.java)
            launch(UI) {
                if (bean.code == "0") {
                    act.itemAdapter.addItem(msg, com.twtstudio.wetalk.View.RIGHT)
                    act.recyclerView.smoothScrollToPosition(act.i)//移动到指定位置
                    act.i++
                } else
                    Toast.makeText(act, bean.msg, Toast.LENGTH_LONG).show()
            }
        }
    }

}
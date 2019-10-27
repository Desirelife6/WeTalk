package com.twtstudio.wetalk.View

import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.twtstudio.wetalk.Model.*
import com.twtstudio.wetalk.Presenter.ItemAdapter
import com.twtstudio.wetalk.Presenter.enableLightStatusBarMode
import com.twtstudio.wetalk.R
import kotlinx.android.synthetic.main.activity_talk.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.warn_popup.view.*
import org.jetbrains.anko.dip
import java.io.BufferedReader
import java.io.InputStreamReader


class TalkActivity : AppCompatActivity() {
    val NETUPDATE = 10
    lateinit var recyclerView: RecyclerView
    lateinit var itemAdapter: ItemAdapter
    var i = 0
    lateinit var handler: Handler
    val t_name = Hawk.get("talkto", "")
    private val pictureList = arrayOf(
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572194006977&di=9b3ff8958ed1f99e525823b21115367c&imgtype=0&src=http%3A%2F%2Fztd00.photos.bdimg.com%2Fztd%2Fw%3D700%3Bq%3D50%2Fsign%3D5a05e4b4890a19d8cb03860503c1f3b6%2F0bd162d9f2d3572c876521d98313632762d0c334.jpg",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572194006977&di=00595d585367a90ef0d2b0cbffe6d405&imgtype=0&src=http%3A%2F%2Fm.magicyourlife101.com%2Fimages%2FT1.Z6BFCpXXXXXXXXX_%2521%25210-item_pic.jpg_360x360.jpg",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572194039726&di=4694a9efee2e05ee63fc608a9d8581dd&imgtype=jpg&src=http%3A%2F%2Fimg.mp.itc.cn%2Fupload%2F20170227%2F03dccef19ec642f58027bb8b39404274.jpeg",
        "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572194006977&di=1628a234ef34b095f21dee53ef3bfbee&imgtype=0&src=http%3A%2F%2Fwww.iopen.com.cn%2Fupload%2F201812%2F18%2F1545127156600591.jpeg"
    )

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_talk)
        initView()

        main_title.text = t_name

        handler = Handler(Handler.Callback { msg ->
            when (msg.what) {
                NETUPDATE -> {
                    addText(msg.obj.toString())
                }
            }
            false
        })

        talkHeartBeatTest(this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initView() {
        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#ededed")
        enableLightStatusBarMode(true)
        recyclerView = findViewById<View>(R.id.rv_talks) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = ItemAdapter(this)
        recyclerView.adapter = itemAdapter
        send_message.setOnClickListener {
            NetService.sendMessageService(
                Hawk.get("userID", ""),
                Hawk.get("token", ""),
                Hawk.get("talkto", ""),
                putMessage.text.toString().trim(),
                this
            )
            putMessage.text.clear()
        }
        add_friends.visibility = View.GONE
        send_picture.setOnClickListener {
            val rando = Calendar.getInstance().get(Calendar.MILLISECOND) % 4

            NetService.sendMessageService(
                Hawk.get("userID", ""),
                Hawk.get("token", ""),
                Hawk.get("talkto", ""),
                pictureList[rando],
                this
            )
        }

    }

    fun addText(msg: String) {
        itemAdapter.addItem(msg, LEFT)
        recyclerView.smoothScrollToPosition(i)//移动到指定位置
        i++
    }

    fun leftClick(v: View) {
        //  第一个参数指定发出内容，第二参数指定发出的是左还是右
        itemAdapter.addItem("右边你好$i", LEFT)
        recyclerView.smoothScrollToPosition(i)//移动到指定位置
        i++
    }

    fun rightClick(v: View) {
        //  第一个参数指定发出内容，第二参数指定发出的是左还是右
        itemAdapter.addItem("左边你好$i", RIGHT)
        recyclerView.smoothScrollToPosition(i)//移动到指定位置
        i++
    }

    fun talkHeartBeatTest(act: TalkActivity) {
        val listenforMessage = Runnable {
            run {
                NetService.ins = messageSocket.getInputStream()
                val reader = BufferedReader(InputStreamReader(NetService.ins))
                val gson = Gson()
                lateinit var str: String
                while (true) {
                    val message = reader.readLine()
                    str = message.toString()
                    Log.d("HHHH", "str")
                    when {
                        str.contains("friendrequest") -> {
                            val bean = gson.fromJson(message, receiveMakeBean::class.java)
                            act.runOnUiThread {
                                act.findViewById<ConstraintLayout>(R.id.talk_layout).post(Runnable {
                                    run {
                                        val popupWindow = PopupWindow(act)
                                        val view = LayoutInflater.from(act)
                                            .inflate(R.layout.warn_popup, null, false)
                                        view.enter_word2.text = bean.from + "请求添加你为好友"
                                        popupWindow.apply {
                                            isFocusable = true
                                            contentView = view
                                            animationStyle =
                                                R.style.style_pop_animation
                                            setBackgroundDrawable(null)
                                            width = act.dip(320)
                                            showAtLocation(
                                                LayoutInflater.from(contentView.context).inflate(
                                                    R.layout.activity_main,
                                                    null
                                                ),
                                                Gravity.CENTER,
                                                0,
                                                0
                                            )
                                            contentView.enter_cancel2.setOnClickListener {
                                                popupWindow.dismiss()
                                            }
                                            contentView.enter_comfirm2.setOnClickListener {
                                                NetService.confirmRequestService2(
                                                    Hawk.get("userID", ""),
                                                    Hawk.get("token", ""),
                                                    bean.from,
                                                    act
                                                )
                                                popupWindow.dismiss()
                                            }
                                        }
                                    }
                                })
                            }

                        }
                        str.contains("makefriendres") -> {
                            val bean = gson.fromJson(message, receiveResBean::class.java)
                            act.runOnUiThread {
                                if (!act.isFinishing) {
                                    Toast.makeText(act, "新的好友已添加", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                        else -> {
                            val bean = gson.fromJson(message, receiveMessageBean::class.java)
                            val temMessage = handler.obtainMessage()
                            temMessage.what = NETUPDATE
                            temMessage.obj = bean.msg
                            if (bean.from == t_name)
                                handler.sendMessage(temMessage)
                        }
                    }
                }
            }
        }
        Thread(listenforMessage).start()
    }

}

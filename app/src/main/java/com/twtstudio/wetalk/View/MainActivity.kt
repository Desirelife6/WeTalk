package com.twtstudio.wetalk.View

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.twtstudio.wetalk.Model.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.dialog_popup.view.*
import kotlinx.android.synthetic.main.warn_popup.view.*
import org.jetbrains.anko.dip
import org.jetbrains.anko.startActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import com.twtstudio.wetalk.Presenter.TabFragment
import com.twtstudio.wetalk.Presenter.enableLightStatusBarMode
import com.twtstudio.wetalk.R
import kotlinx.coroutines.android.UI
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {


    val NETUPDATE = 10
    private lateinit var mViewPager: ViewPager
    val mTabList = ArrayList<TabFragment>()
    private val mTitles = arrayOf("聊天", "通讯录", "我的")
    private var status: Int = LOGOUT
    lateinit var handler: Handler
    val listenforMessage = Runnable {
        run {
            NetService.ins = listenSocket.getInputStream()
            val reader = BufferedReader(InputStreamReader(NetService.ins))
            val gson = Gson()
            while (true) {
                val message = reader.readLine()
                when {
                    message.contains("friendrequest") -> {
                        val bean = gson.fromJson(message, receiveMakeBean::class.java)
                        val temMessage = handler.obtainMessage()
                        temMessage.what = NETUPDATE
                        temMessage.obj = bean.from
                        handler.sendMessage(temMessage)
                    }
                    message.contains("makefriendsres") -> {
                        val bean = gson.fromJson(message, receiveResBean::class.java)
                        handler.post {
                            if (!this.isFinishing) {
                                Toast.makeText(this, "新的好友已添加", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else -> {
                        val bean = gson.fromJson(message, receiveMessageBean::class.java)
                        var flag = false
                        for (i in MessageToRead) {
                            if (i.name == bean.from) {
                                i.messages.add(showBean(bean.msg, bean.time, LEFT))
                                flag = true
                            }
                        }
                        if (!flag) {
                            val temp = ReadBean(bean.from)
                            temp.messages.add(showBean(bean.msg, bean.time, LEFT))
                            MessageToRead.add(temp)
                        }
                        launch(UI) {
                            if (!bean.msg.contains("https://"))
                                Toast.makeText(
                                    this@MainActivity,
                                    "${bean.from}说：${bean.msg}",
                                    Toast.LENGTH_LONG
                                ).show()
                            else
                                Toast.makeText(
                                    this@MainActivity,
                                    "${bean.from}给您发送了照片",
                                    Toast.LENGTH_LONG
                                ).show()
                        }
                    }
                }
                Log.d("HHHH", message)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        Hawk.init(this).build()
        handler = Handler(Handler.Callback { msg ->
            when (msg.what) {
                NETUPDATE -> {
                    runOnUiThread {
                        //showWarnWindow(msg.obj.toString())
                        NetService.confirmRequestService(
                            Hawk.get("userID", ""),
                            Hawk.get("token", ""),
                            msg.obj.toString(),
                            this@MainActivity
                        )
                        Toast.makeText(this, "${msg.obj}已经添加你为好友", Toast.LENGTH_LONG).show()
                    }
                }
            }
            false
        })
        status = Hawk.get("Status", LOGOUT)
        if (status == LOGOUT) {
            startActivity<LoginActivity>()
            finish()
        }
        mainHeartBeatTest(this)
    }


    private fun initView() {
        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#ededed")
        enableLightStatusBarMode(true)
        initMyViewPager()
        setOnListener()
    }

    private fun setOnListener() {
        main_talks.setOnClickListener {
            mViewPager.setCurrentItem(0, false)
        }
        main_friends.setOnClickListener {
            mViewPager.setCurrentItem(1, false)
        }
        main_mine.setOnClickListener {
            mViewPager.setCurrentItem(2, false)
        }

        main_refresh.setOnClickListener {
            NetService.friendsService(
                Hawk.get("userID", ""),
                Hawk.get("token", ""),
                this@MainActivity
            )
        }

        add_friends.setOnClickListener {
            val popupWindow = PopupWindow(this)
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_popup, null, false)
            view.enter_word.text = "添加好友"
            popupWindow.apply {
                isFocusable = true
                contentView = view
                animationStyle = R.style.style_pop_animation
                setBackgroundDrawable(null)
                width = dip(320)
                showAtLocation(
                    LayoutInflater.from(contentView.context).inflate(R.layout.activity_main, null),
                    Gravity.CENTER,
                    0,
                    0
                )
                contentView.enter_cancel.setOnClickListener {
                    popupWindow.dismiss()
                }
                contentView.enter_comfirm.setOnClickListener {
                    popupWindow.dismiss()
                    NetService.makeFriendsService(
                        Hawk.get("userID", ""),
                        Hawk.get("token", ""),
                        contentView.inputUser.text.toString(),
                        this@MainActivity
                    )
                }
            }
        }

    }

    private fun initMyViewPager() {
        mViewPager = findViewById(R.id.main_viewpager)
        for (i in mTitles.indices) {
            val fragment = TabFragment()
            val bundle = Bundle()
            bundle.putString("title", mTitles[i])
            fragment.arguments = bundle
            mTabList.add(fragment)
        }

        val fm = supportFragmentManager
        val mAdapter = object : FragmentPagerAdapter(fm) {
            override fun getItem(position: Int): Fragment {
                return mTabList[position]
            }

            override fun getCount(): Int {
                return mTabList.size
            }
        }
        mViewPager.adapter = mAdapter

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                when (position) {
                    0 -> {
                        main_title.text = "微信"
                        main_talks.setImageResource(R.mipmap.liaotian_check)
                        main_friends.setImageResource(R.mipmap.txl_common)
                        main_mine.setImageResource(R.mipmap.mine_common)
                        mTabList[0].setTalkList()
                    }
                    1 -> {
                        main_title.text = "通讯录"
                        main_talks.setImageResource(R.mipmap.liaotian_common)
                        main_friends.setImageResource(R.mipmap.txl_check)
                        main_mine.setImageResource(R.mipmap.mine_common)
                    }
                    else -> {
                        main_title.text = "个人信息"
                        main_talks.setImageResource(R.mipmap.liaotian_common)
                        main_friends.setImageResource(R.mipmap.txl_common)
                        main_mine.setImageResource(R.mipmap.mine_check)
                        mTabList[2].setMineList(this@MainActivity)
                    }
                }
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    fun showWarnWindow(msg: String) {
        val popupWindow = PopupWindow(this)
        val view = LayoutInflater.from(this)
            .inflate(R.layout.warn_popup, null, false)
        view.enter_word2.text = msg + "请求添加你为好友"
        popupWindow.apply {
            isFocusable = true
            contentView = view
            animationStyle = R.style.style_pop_animation
            setBackgroundDrawable(null)
            width = dip(320)
            runOnUiThread {
                Runnable {
                    run {
                        showAtLocation(
                            LayoutInflater.from(contentView.context).inflate(
                                R.layout.activity_main,
                                null
                            ),
                            Gravity.CENTER,
                            0,
                            0
                        )
                    }
                }
            }
            contentView.enter_cancel2.setOnClickListener {
                popupWindow.dismiss()
            }
            contentView.enter_comfirm2.setOnClickListener {
                NetService.confirmRequestService(
                    Hawk.get("userID", ""),
                    Hawk.get("token", ""),
                    msg,
                    this@MainActivity
                )
                popupWindow.dismiss()
            }
        }
    }


    fun mainHeartBeatTest(act: MainActivity) {
        Thread(listenforMessage).start()
    }


}



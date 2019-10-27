package com.twtstudio.wetalk.View

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.twtstudio.wetalk.Model.NetService
import com.twtstudio.wetalk.Presenter.enableLightStatusBarMode
import com.twtstudio.wetalk.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.common_toolbar.*
import kotlinx.android.synthetic.main.warn_popup.view.*
import org.jetbrains.anko.dip


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()
        window.statusBarColor = Color.parseColor("#ededed")
        //NetService.mainHeartBeatTest()
        enableLightStatusBarMode(true)
        login_btn_main.setOnClickListener {
            NetService.loginService(putID.text.toString(),putPassword.text.toString(),this)
        }
        regist_btn_main.setOnClickListener{
            NetService.regiService(putID.text.toString(),putPassword.text.toString(),this)
        }
        main_refresh.visibility = View.GONE
        add_friends.visibility = View.GONE
    }

    fun showPopWindow(msg:String){
        val popupWindow = PopupWindow(this)
        val view = LayoutInflater.from(this).inflate(R.layout.warn_popup, null, false)
        view.enter_word2.text = msg
        popupWindow.apply {
            isFocusable = true
            contentView = view
            animationStyle = R.style.style_pop_animation
            setBackgroundDrawable(null)
            width = dip(320)
            showAtLocation(
                LayoutInflater.from(contentView.context).inflate(R.layout.activity_login, null),
                Gravity.CENTER,
                0,
                0
            )
            contentView.enter_cancel2.setOnClickListener {
                popupWindow.dismiss()
            }
            contentView.enter_comfirm2.setOnClickListener {
                popupWindow.dismiss()
            }
        }
    }
}

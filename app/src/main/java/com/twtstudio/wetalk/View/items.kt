package com.twtstudio.wetalk.View

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.edu.twt.retrox.recyclerviewdsl.Item
import cn.edu.twt.retrox.recyclerviewdsl.ItemController
import com.orhanobut.hawk.Hawk
import com.twtstudio.wetalk.Model.NetService
import kotlinx.android.synthetic.main.friends_item.view.*
import kotlinx.android.synthetic.main.mine_item.view.*
import kotlinx.android.synthetic.main.talk_info_item.view.*
import kotlinx.android.synthetic.main.talk_info_item.view.iv_avatar
import kotlinx.android.synthetic.main.user_info_item.view.*
import org.jetbrains.anko.layoutInflater
import org.jetbrains.anko.startActivity


const val LOGOUT = 0
const val LOGIN = 1
const val LEFT = 2
const val RIGHT = 3

class TalkItem(
    val name: String?,
    val message: String?,
    val iv: String?,
    val id: String,
    val date: String
) : Item {

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(com.twtstudio.wetalk.R.layout.talk_info_item, parent, false)
            return com.twtstudio.wetalk.View.TalkItem.ViewHolder(
                view,
                view.iv_avatar,
                view.tv_username,
                view.tv_message,
                view.tv__date
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as com.twtstudio.wetalk.View.TalkItem.ViewHolder
            item as com.twtstudio.wetalk.View.TalkItem
            holder.name.text = item.name
            holder.message.text = item.message
            holder.date.text = item.date
            holder.view.setOnClickListener {
                it.context.startActivity<TalkActivity>()
                Hawk.put("talkto",item.name)
            }
        }
    }

    class ViewHolder(
        val view: View,
        val imageView: ImageView,
        val name: TextView,
        val message: TextView,
        val date: TextView
    ) :
        RecyclerView.ViewHolder(view)

    override val controller = com.twtstudio.wetalk.View.TalkItem.Companion
}

class IntroItem(val name: String?, val act: Activity) : Item {

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(com.twtstudio.wetalk.R.layout.mine_item, parent, false)
            return com.twtstudio.wetalk.View.IntroItem.ViewHolder(
                view,
                view.iv_icon,
                view.tv_intro
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as com.twtstudio.wetalk.View.IntroItem.ViewHolder
            item as com.twtstudio.wetalk.View.IntroItem
            holder.name.text = item.name
            holder.view.setOnClickListener {
                if (it.tv_intro.text == "退出") {
                    NetService.logoutService(
                        Hawk.get("userID", ""),
                        Hawk.get("token", ""),
                        item.act
                    )
                    Hawk.put("Status", com.twtstudio.wetalk.View.LOGOUT)
                    item.act.startActivity<LoginActivity>()
                    item.act.finish()
                }
            }
        }
    }

    class ViewHolder(
        val view: View,
        val imageView: ImageView,
        val name: TextView
    ) :
        RecyclerView.ViewHolder(view)

    override val controller = com.twtstudio.wetalk.View.IntroItem.Companion
}

class ProfileItem(
    val user_avatar: String?,
    val id: String
) : Item {

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(com.twtstudio.wetalk.R.layout.user_info_item, parent, false)
            return com.twtstudio.wetalk.View.ProfileItem.ViewHolder(
                view,
                view.sdv_avatar,
                view.tv_wxid
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as com.twtstudio.wetalk.View.ProfileItem.ViewHolder
            item as com.twtstudio.wetalk.View.ProfileItem
            holder.wxid.text = item.id
        }
    }

    class ViewHolder(
        val view: View,
        val avatar: ImageView,
        val wxid: TextView
    ) :
        RecyclerView.ViewHolder(view)

    override val controller = com.twtstudio.wetalk.View.ProfileItem.Companion
}

class FriendItem(val avatar: String, val name: String) : Item {

    companion object : ItemController {
        override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val inflater = parent.context.layoutInflater
            val view = inflater.inflate(com.twtstudio.wetalk.R.layout.friends_item, parent, false)
            return com.twtstudio.wetalk.View.FriendItem.ViewHolder(
                view,
                view.iv_avatar2,
                view.tv_name2,
                view.friend_delete
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Item) {
            holder as com.twtstudio.wetalk.View.FriendItem.ViewHolder
            item as com.twtstudio.wetalk.View.FriendItem
            holder.name.text = item.name
            holder.view.setOnClickListener {
                it.context.startActivity<TalkActivity>()
                Hawk.put("talkto",item.name)
            }
            holder.delete.setOnClickListener {
                NetService.deleteService(Hawk.get("userID",""),Hawk.get("token",""),item.name, holder.view.context)
            }
        }
    }

    class ViewHolder(
        val view: View,
        val imageView: ImageView,
        val name: TextView,
        val delete:ImageView
    ) :
        RecyclerView.ViewHolder(view)

    override val controller = com.twtstudio.wetalk.View.FriendItem.Companion
}

fun MutableList<Item>.setTalkItem(
    name: String?,
    message: String?,
    iv: String?,
    id: String,
    date: String
) =
    add(com.twtstudio.wetalk.View.TalkItem(name, message, iv, id, date))

fun MutableList<Item>.setIntroItem(name: String?, act: Activity) =
    add(com.twtstudio.wetalk.View.IntroItem(name, act))

fun MutableList<Item>.setProfileItem(avatar: String?, name: String) =
    add(com.twtstudio.wetalk.View.ProfileItem(avatar, "微信号：$name"))

fun MutableList<Item>.setFriendItem(avatar: String, name: String) =
    add(com.twtstudio.wetalk.View.FriendItem(avatar, name))


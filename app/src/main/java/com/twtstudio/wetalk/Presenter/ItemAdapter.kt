package com.twtstudio.wetalk.Presenter


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.twtstudio.wetalk.R
import com.twtstudio.wetalk.View.itemBean
import com.twtstudio.wetalk.View.showBean

import java.util.ArrayList

class ItemAdapter(private var context: Context) :
    RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    private var list: MutableList<showBean> = ArrayList()
    private var leftOrRight: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            LayoutInflater.from(
                context
            ).inflate(
                R.layout.message_item, parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val str =  list[position].time.split(":")
        if(str[1].length == 1){
            holder.tvtime.text = "${str[0]}:0${str[1]}"
        }else{
            holder.tvtime.text = "${str[0]}:${str[1]}"
        }

        if (list[position].loc == com.twtstudio.wetalk.View.LEFT) {
            if(list[position].text.contains("https://")){
                holder.tvLeft.visibility = View.GONE
                holder.tvRight.visibility = View.GONE
                holder.ivLeft.visibility = View.VISIBLE
                holder.ivRight.visibility = View.GONE
                Glide.with(holder.view)
                    .load(list[position].text)
                    .centerCrop()
                    .into(holder.ivLeft)
            }else{
                holder.tvLeft.visibility = View.VISIBLE
                holder.tvRight.visibility = View.GONE
                holder.ivLeft.visibility = View.GONE
                holder.ivRight.visibility = View.GONE
                holder.tvLeft.text = list[position].text
            }

        } else if (list[position].loc == com.twtstudio.wetalk.View.RIGHT) {
            if(list[position].text.contains("https://")){
                holder.tvLeft.visibility = View.GONE
                holder.tvRight.visibility = View.GONE
                holder.ivLeft.visibility = View.GONE
                holder.ivRight.visibility = View.VISIBLE
                Glide.with(holder.view)
                    .load(list[position].text)
                    .centerCrop()
                    .into(holder.ivRight)
            }else{
                holder.tvLeft.visibility = View.GONE
                holder.tvRight.visibility = View.VISIBLE
                holder.ivLeft.visibility = View.GONE
                holder.ivRight.visibility = View.GONE
                holder.tvRight.text = list[position].text
            }
        }
    }

    //添加子项
    fun addItem(str: showBean) {
        list.add(str)
        notifyItemInserted(list.size - 1)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var view: View = itemView.findViewById(R.id.message_view) as View
        var tvLeft: TextView = itemView.findViewById<View>(R.id.leftTv) as TextView
        var tvRight: TextView = itemView.findViewById<View>(R.id.rightTv) as TextView
        var ivLeft: ImageView = itemView.findViewById<View>(R.id.leftIv) as ImageView
        var ivRight: ImageView = itemView.findViewById<View>(R.id.rightIv) as ImageView
        var tvtime: TextView = itemView.findViewById(R.id.message_date) as TextView
    }
}

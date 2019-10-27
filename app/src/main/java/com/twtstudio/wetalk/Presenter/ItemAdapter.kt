package com.twtstudio.wetalk.Presenter


import android.content.Context
import android.icu.util.Calendar
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

import java.util.ArrayList

class ItemAdapter(private var context: Context) :
    RecyclerView.Adapter<ItemAdapter.MyViewHolder>() {

    private var list: MutableList<String> = ArrayList()
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
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        holder.tvtime.text = "$hour:$minute"
        if (leftOrRight == com.twtstudio.wetalk.View.LEFT) {
            if(list[position].contains("https://")){
                holder.tvLeft.visibility = View.GONE
                holder.tvRight.visibility = View.GONE
                holder.ivLeft.visibility = View.VISIBLE
                holder.ivRight.visibility = View.GONE
                Glide.with(holder.view)
                    .load(list[position])
                    .centerCrop()
                    .into(holder.ivLeft)
            }else{
                holder.tvLeft.visibility = View.VISIBLE
                holder.tvRight.visibility = View.GONE
                holder.ivLeft.visibility = View.GONE
                holder.ivRight.visibility = View.GONE
                holder.tvLeft.text = list[position]
            }

        } else if (leftOrRight == com.twtstudio.wetalk.View.RIGHT) {
            if(list[position].contains("https://")){
                holder.tvLeft.visibility = View.GONE
                holder.tvRight.visibility = View.GONE
                holder.ivLeft.visibility = View.GONE
                holder.ivRight.visibility = View.VISIBLE
                Glide.with(holder.view)
                    .load(list[position])
                    .centerCrop()
                    .into(holder.ivRight)
            }else{
                holder.tvLeft.visibility = View.GONE
                holder.tvRight.visibility = View.VISIBLE
                holder.ivLeft.visibility = View.GONE
                holder.ivRight.visibility = View.GONE
                holder.tvRight.text = list[position]
            }
        }
    }

    //添加子项
    fun addItem(str: String, leftOrRight: Int) {
        this.leftOrRight = leftOrRight
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

package com.example.whatsup.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsup.R
import com.example.whatsup.model.UserData
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat

class ChatsAdapter(private val context: Context):
    RecyclerView.Adapter<ChatsAdapter.ChatsHolder>() {

    private lateinit var mlistener: ListAdapterListener

    interface ListAdapterListener{
        fun onClickChat(chat: String,phone: String)
        fun onClickDp(name:String?,uri:String?)
    }


    fun setOnclick(listener: ListAdapterListener){
        mlistener = listener
    }

    val chatsList =  ArrayList<UserData>()

    inner class ChatsHolder(view: View,listener:ListAdapterListener):RecyclerView.ViewHolder(view){
        val dp = view.findViewById<ImageView>(R.id.chatDP)
        val name = view.findViewById<TextView>(R.id.userNameTitle)
        val lastMsg = view.findViewById<TextView>(R.id.chatLastMsg)
        val layout = view.findViewById<LinearLayout>(R.id.chatClick)
        val time = view.findViewById<TextView>(R.id.timeStampChat)
        val count = view.findViewById<TextView>(R.id.messageCount)

        init {
            layout.setOnClickListener {
                listener.onClickChat(chatsList[adapterPosition].userName,chatsList[adapterPosition].phoneNo)
            }
            dp.setOnClickListener {
                listener.onClickDp(chatsList[adapterPosition].userName,chatsList[adapterPosition].phoneNo)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.chats_view,parent,false)
        return ChatsHolder(view,mlistener)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {

        val current = chatsList[position]
        val storageReference = FirebaseStorage.getInstance("gs://whats-up-1e69b.appspot.com")
            .getReference("${current.phoneNo}/DP")
        storageReference.downloadUrl.addOnSuccessListener {
            Glide.with(context)
                .load(it).circleCrop()
                .into(holder.dp)
            Log.i("DownloadUri",it.toString())
        }
        val timeFormat = SimpleDateFormat("hh:mm aaa")
        val time = Date(current.time.toString().toLong()*1000)
        timeFormat.format(time)
        holder.name.text = current.userName
        holder.lastMsg.text = current.lastMeg
        holder.time.text = timeFormat.format(time).toString()
        if(current.count != 0){
            holder.count.text = current.count.toString()
            holder.count.setBackgroundResource(R.drawable.message_count_circle)
            holder.time.setTextColor(R.color.purple_500)
        }
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    fun upDate(newList: List<UserData>){
        chatsList.clear()
        Log.i("newList",newList.toString())
        chatsList.addAll(newList.sortedByDescending { it.time.toString().toInt() })
        notifyDataSetChanged()
    }

}
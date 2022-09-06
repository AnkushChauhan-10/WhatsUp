package com.example.whatsup.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.contentValuesOf
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsup.R
import com.example.whatsup.model.UserData

class ChatsAdapter(private val context: Context):
    RecyclerView.Adapter<ChatsAdapter.ChatsHolder>() {

    private lateinit var mlistener: ListAdapterListener

    interface ListAdapterListener{
        fun onClickChat(chat: String,phone: String)
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
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.chats_view,parent,false)
        return ChatsHolder(view,mlistener)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ChatsHolder, position: Int) {
        val current = chatsList[position]
        holder.name.text = current.userName
        holder.lastMsg.text = current.lastMeg
        holder.time.text = current.time
    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    fun upDate(newList: List<UserData>){
        chatsList.clear()
        chatsList.addAll(newList)
        notifyDataSetChanged()
    }

}
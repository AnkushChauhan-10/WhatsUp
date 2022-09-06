package com.example.whatsup.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsup.R
import com.example.whatsup.model.ContactsModel
import com.example.whatsup.model.MessageModel
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val context: Context,private val user: String):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val messageList =  ArrayList<MessageModel>()

    inner class ReciverHolder(view: View): RecyclerView.ViewHolder(view){
        val msg = view.findViewById<TextView>(R.id.reciverMessageBox)
        val time = view.findViewById<TextView>(R.id.reciverTime)
    }

    inner class SenderHolder(view: View): RecyclerView.ViewHolder(view){
        val msg = view.findViewById<TextView>(R.id.senderMessageBox)
        val time = view.findViewById<TextView>(R.id.senderTime)
        val seen = view.findViewById<ImageView>(R.id.messageSeen)
    }

    companion object {
        private const val SENDER_BOX = 0
        private const val RECEIVER_BOX = 1
    }

    override fun getItemViewType(position: Int): Int {
        if(messageList[position].senderPhone.equals(user)){
            return SENDER_BOX
        }else{
            return RECEIVER_BOX
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       var messageModel = messageList[position]
        if(holder.javaClass == SenderHolder::class.java){
            (holder as SenderHolder).msg.text = messageModel.msg
            (holder as SenderHolder).time.text = messageModel.time
            if(messageModel.seen == true){
                (holder as SenderHolder).seen.setImageResource(R.drawable.ic_eye_seen)
            }else{
                (holder as SenderHolder).seen.setImageResource(R.drawable.ic_eye_not_seen)
            }
        }else{
            (holder as ReciverHolder).msg.text = messageModel.msg
            (holder as ReciverHolder).time.text = messageModel.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == SENDER_BOX){
            var view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false)
            return SenderHolder(view)
        }else{
            var view = LayoutInflater.from(context).inflate(R.layout.reciver_layout,parent,false)
            return ReciverHolder(view)
        }
    }

    fun update(newList: List<MessageModel>){
        Log.i("update",newList.toString()+"contact")
        messageList.clear()
        messageList.addAll(newList)
        notifyDataSetChanged()
    }


}
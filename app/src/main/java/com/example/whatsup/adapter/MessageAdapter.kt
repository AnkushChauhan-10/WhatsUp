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
import com.google.android.gms.common.data.DataHolder
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text
import java.sql.Date
import java.text.SimpleDateFormat

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
    inner class DateHolder(view: View):RecyclerView.ViewHolder(view){
        val dateText = view.findViewById<TextView>(R.id.messageDateText)
    }

    companion object {
        private const val SENDER_BOX = 0
        private const val RECEIVER_BOX = 1
        private const val DATE_BOX = 2
    }

    override fun getItemViewType(position: Int): Int {
        if(messageList[position].senderPhone.equals("DateBox")){
            Log.i("LastMess",messageList[position].time.toString())
            return DATE_BOX
        }
        else if(messageList[position].senderPhone.equals(user)){
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

        if(holder.javaClass == DateHolder::class.java){
            (holder as DateHolder).dateText.text = getDate(messageModel.time.toString())
        }
        else if(holder.javaClass == SenderHolder::class.java){
            (holder as SenderHolder).msg.text = messageModel.msg
            (holder as SenderHolder).time.text = SimpleDateFormat("hh:mm aaa")
                .format(messageModel.time.toString().toLong()*1000)
            if(messageModel.seen == true){
                (holder as SenderHolder).seen.setImageResource(R.drawable.ic_eye_seen)
            }else{
                (holder as SenderHolder).seen.setImageResource(R.drawable.ic_eye_not_seen)
            }
        }else if(holder.javaClass == ReciverHolder::class.java){
            (holder as ReciverHolder).msg.text = messageModel.msg
            (holder as ReciverHolder).time.text = SimpleDateFormat("hh:mm aaa")
                .format(messageModel.time.toString().toLong()*1000)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == DATE_BOX){
            var view = LayoutInflater.from(context).inflate(R.layout.message_date_view,parent,false)
            return DateHolder(view)
        }
        else if(viewType == SENDER_BOX){
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
        messageList.addAll(setMap(newList))
        notifyDataSetChanged()
    }

    private fun setMap(list:List<MessageModel>):List<MessageModel>{
        var lastDate = "0"
        var newList = ArrayList<MessageModel>()
        for(mess in list){
            if(!getDateBox(lastDate,mess.time.toString())){
                lastDate = mess.time.toString()
                newList.add(MessageModel(senderPhone = "DateBox",time = lastDate))
            }
            newList.add(mess)
        }
        return newList
    }

    fun getDateBox(lastTime:String,messTime:String): Boolean{
        try{
        val timeInt= SimpleDateFormat("yyyy:M:dd").format(lastTime.toLong()*1000).toString()
        val messInt = SimpleDateFormat("yyyy:M:dd").format(messTime.toLong()*1000).toString()
            if(timeInt.equals(messInt)){
                return true
            }
            return false
        }
        catch (e:Exception){
            return false
        }
    }

    private fun getDate(time :String):String{

        var ans = SimpleDateFormat("dd/M/yyyy").format(Date(time.toLong()*1000)).toString()

        var currentTime = System.currentTimeMillis()/1000
        val dateFormat = SimpleDateFormat("yyyyMdd")

        val today = dateFormat.format(Date(currentTime.toString().toLong()*1000)).toInt()
        val timeStamp = dateFormat.format(Date(time.toLong()*1000)).toInt()


        if(today == timeStamp){
            ans = "Today"
        }else if((today - timeStamp)==1){
            ans = "Yesterday"
        }
        return ans
    }

}
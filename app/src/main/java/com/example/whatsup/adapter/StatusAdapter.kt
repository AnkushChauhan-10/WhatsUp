package com.example.whatsup.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsup.R
import com.example.whatsup.model.ContactsModel
import com.example.whatsup.model.StatusDetails
import java.sql.Date
import java.text.SimpleDateFormat

class StatusAdapter(private val context: Context,private val contactList:List<ContactsModel>):RecyclerView.Adapter<StatusAdapter.StatusHolder>() {

    private lateinit var mlistener: ListAdapterListener
    private  var new:Boolean = false
    private  var old:Boolean = false

    interface ListAdapterListener{
        fun onClickStatus(phone: String?,name:String?)
    }


    fun setOnclick(listener: ListAdapterListener){
        mlistener = listener
    }

    val chatsList =  ArrayList<StatusDetails>()

    inner class StatusHolder(view: View,listener: StatusAdapter.ListAdapterListener): RecyclerView.ViewHolder(view){
        val dp = view.findViewById<ImageView>(R.id.statusImg)
        val name = view.findViewById<TextView>(R.id.statusUserName)
        val layout = view.findViewById<LinearLayout>(R.id.statusClick)
        val time = view.findViewById<TextView>(R.id.timeStatus)
        val outer = view.findViewById<LinearLayout>(R.id.status)

        init {
            layout.setOnClickListener {
                listener.onClickStatus(chatsList[adapterPosition].phone,name.text.toString())
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatusHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.status_view,parent,false)
        return StatusHolder(view,mlistener)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: StatusHolder, position: Int) {

        val current = chatsList[position]
        holder.outer.removeAllViews()

        holder.time.text = getTime(current.timeSpan.toString())

        Glide.with(context).load(current.uri).circleCrop().into(holder.dp)

        if(!current.seen.toString().toBoolean()){
            if(!new){
                var state = TextView(context)
                state.textSize = 14f
                state.text = "Recent update"
                val param = holder.outer.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,0,0,20)
                holder.outer.layoutParams = param
                state.setTextColor(ContextCompat.getColor(context,R.color.black))
                holder.outer.addView(state)
                new=true
            }
            holder.dp.background = context.resources.getDrawable(R.drawable.upload_file_circle)

        }else{
            if(!old){
                var state = TextView(context)
                state.textSize = 14f
                state.text = "Viewed update"
                state.setTextColor(ContextCompat.getColor(context,R.color.black))
                val param = holder.outer.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(0,0,0,20)
                holder.outer.layoutParams = param
                holder.outer.addView(state)
                old=true
            }
            holder.dp.background = context.resources.getDrawable(R.drawable.circuler_image_shape)
        }

        holder.name.text = current.phone
        for(contact in contactList){
            if(contact.phone == current.phone){
                holder.name.text = contact.name
                break
            }
        }

    }

    override fun getItemCount(): Int {
        return chatsList.size
    }

    fun upDate(newList: List<StatusDetails>){
        chatsList.clear()
        new = false
        old = false
        Log.i("newList",newList.toString())
        chatsList.addAll(newList.sortedByDescending { it.timeSpan.toString().toInt() })
        notifyDataSetChanged()
    }


    private fun getTime(time :String):String{

        var ans:String
        var currentTime = System.currentTimeMillis()/1000

        val dayFormat = SimpleDateFormat("EEE")
        val timeFormat = SimpleDateFormat("hh:mm aaa")

        val today = dayFormat.format(Date(currentTime.toString().toLong()*1000))
        val timeStamp = dayFormat.format(Date(time.toLong()*1000))

        Log.i("Today",(time.toLong()*1000).toString()+" and " + (System.currentTimeMillis()).toString())

        if(today == timeStamp){
            ans = "Today, " + timeFormat.format(Date(time.toLong()*1000)).toString()
        }else{
            ans = "Yesterday, " + timeFormat.format(Date(time.toLong()*1000)).toString()
        }

        return ans
    }

}
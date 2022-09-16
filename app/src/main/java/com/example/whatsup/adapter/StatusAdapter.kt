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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsup.R
import com.example.whatsup.model.ContactsModel
import com.example.whatsup.model.StatusDetails

class StatusAdapter(private val context: Context,private val contactList:List<ContactsModel>):RecyclerView.Adapter<StatusAdapter.StatusHolder>() {

    private lateinit var mlistener: ListAdapterListener

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

        Glide.with(context).load(current.uri).circleCrop().into(holder.dp)
        if(!current.seen.toString().toBoolean()){
            holder.dp.background = context.resources.getDrawable(R.drawable.upload_file_circle)
        }else{
            holder.dp.background = context.resources.getDrawable(R.drawable.circuler_image_shape)
        }
        holder.time.text = current.timeSpan.toString()
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
        Log.i("newList",newList.toString())
        chatsList.addAll(newList)
        notifyDataSetChanged()
    }

}
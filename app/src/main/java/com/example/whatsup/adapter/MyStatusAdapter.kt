package com.example.whatsup.adapter

import android.content.Context
import android.media.Image
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
import com.example.whatsup.model.StatusModel

class MyStatusAdapter(private val context: Context):RecyclerView.Adapter<MyStatusAdapter.MyStatusHolder>() {

    val allStatusList = ArrayList<StatusModel>()

    private lateinit var mlistener: MyStatusAdapter.ListAdapterListener
    interface ListAdapterListener{
        fun onClickStatus(position: Int)
    }
    fun setOnclick(listener: MyStatusAdapter.ListAdapterListener){
        mlistener = listener
    }

    inner class MyStatusHolder(view: View,listener: MyStatusAdapter.ListAdapterListener):RecyclerView.ViewHolder(view){

        val img = view.findViewById<ImageView>(R.id.myStatusPic)
        val totalView = view.findViewById<TextView>(R.id.totalView)
        val delete = view.findViewById<ImageView>(R.id.deleteMyStatus)
        val time = view.findViewById<TextView>(R.id.timeStatus)
        val text = view.findViewById<TextView>(R.id.myStatusLastText)
        val click = view.findViewById<LinearLayout>(R.id.muStatusClick)

        init {
            click.setOnClickListener {
               listener.onClickStatus(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStatusHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.my_status_view,parent,false)
        return MyStatusHolder(view,mlistener)
    }

    override fun onBindViewHolder(holder: MyStatusHolder, position: Int) {
        holder.text.visibility = ViewGroup.GONE
        var currentStatus = allStatusList[position]
        Glide.with(context).load(currentStatus.uri).centerCrop().circleCrop().into(holder.img)
        holder.time.text = currentStatus.timeSpan.toString()
        if(position == allStatusList.size-1){
            holder.text.visibility = ViewGroup.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return allStatusList.size
    }

    fun update(newList: List<StatusModel>){
        Log.i("update",newList.toString()+"contact")
        allStatusList.clear()
        allStatusList.addAll(newList)
        notifyDataSetChanged()
    }

}
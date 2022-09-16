package com.example.whatsup.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.whatsup.R
import com.example.whatsup.model.ContactsModel
import com.example.whatsup.model.StatusModel
import kotlinx.coroutines.CoroutineScope
import org.w3c.dom.Text

class ShowStatusViewPagerAdapter(private val context: Context): RecyclerView.Adapter<ShowStatusViewPagerAdapter.ViewPager2Holder>() {


    val statusList = ArrayList<StatusModel>()

    inner class ViewPager2Holder(view: View):RecyclerView.ViewHolder(view){
        val img = view.findViewById<ImageView>(R.id.show_Status)
        val text = view.findViewById<TextView>(R.id.statusText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.status_view_pager,parent,false)
        return ViewPager2Holder(view)
    }

    override fun onBindViewHolder(holder: ViewPager2Holder, position: Int) {
        val currentStatus = statusList[position]
        holder.text.text = currentStatus.text
        Glide.with(context).load(currentStatus.uri).fitCenter().centerCrop()
            .into(holder.img)
    }

    override fun getItemCount(): Int {
        return statusList.size
    }

    fun update(newList: List<StatusModel>){
        statusList.clear()
        statusList.addAll(newList)
        notifyDataSetChanged()
    }

}
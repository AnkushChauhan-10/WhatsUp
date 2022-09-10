package com.example.whatsup.adapter

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
import com.google.firebase.storage.FirebaseStorage

class ContactAdapter(private val context: Context):
    RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    private lateinit var mlistener: ListAdapterListener

    interface ListAdapterListener{
        fun onClickContact(chat: String?,phone: String?)
    }


    fun setOnclick(listener: ListAdapterListener){
        mlistener = listener
    }

    val contactList = ArrayList<ContactsModel>()

    inner class ContactHolder(view: View, listener: ListAdapterListener):RecyclerView.ViewHolder(view){
        val name = view.findViewById<TextView>(R.id.contactName)
        val dp = view.findViewById<ImageView>(R.id.contactDP)
        val layout = view.findViewById<LinearLayout>(R.id.contactClick)

        init {
            layout.setOnClickListener {
                listener.onClickContact(contactList[adapterPosition].name,contactList[adapterPosition].phone)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_view,parent,false)
        return ContactHolder(view,mlistener)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val current = contactList[position]
        val storageReference = FirebaseStorage.getInstance("gs://whats-up-1e69b.appspot.com").getReference(current.phone.toString())
        storageReference.downloadUrl.addOnSuccessListener {
            Glide.with(context)
                .load(it).circleCrop()
                .into(holder.dp)
            Log.i("DownloadUri",it.toString())
        }
        holder.name.text = current.name
    }

    override fun getItemCount(): Int {
       return contactList.size
    }

    fun update(newList: List<ContactsModel>){
        Log.i("update",newList.toString()+"contact")
        contactList.clear()
        contactList.addAll(newList)
        notifyDataSetChanged()
    }

}
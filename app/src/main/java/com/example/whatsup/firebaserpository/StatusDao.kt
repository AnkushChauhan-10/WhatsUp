package com.example.whatsup.firebaserpository

import android.annotation.SuppressLint
import android.app.Application
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.whatsup.model.ContactsModel
import com.example.whatsup.model.MessageModel
import com.example.whatsup.model.StatusDetails
import com.example.whatsup.model.StatusModel
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

@SuppressLint("Range")
class StatusDao(val application: Application) {

    val database = Firebase.database
    val storage = FirebaseStorage.getInstance("gs://whats-up-1e69b.appspot.com").reference
    val allContactList = ArrayList<ContactsModel>()

    init {
        val phone = application.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
           while (phone!!.moveToNext()) {
               var number =
                   phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
               number = phoneNo(number)
               var flag = 0
               var name =
                   phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
               allContactList.forEach { it ->
                   if (it.phone == number) {
                       flag = 1
                   }
               }
               if (flag == 0) {
                           allContactList.add(ContactsModel(name, "a", "s", number))
                           Log.i(number, name)
               } else {
                   Log.i(number, "noPresent")
               }
           }
        getContactList()
    }

    private val _users = MutableLiveData<List<ContactsModel>>()
    val users : LiveData<List<ContactsModel>> = _users

    fun getContactList(){
        val dataBaseReference = database.getReference("Users")
        var list = ArrayList<ContactsModel>()
        for(user in allContactList){
            dataBaseReference.child(user.phone.toString()).get().addOnSuccessListener {
                if(it.exists()){
                    list.add(user)
                    _users.value = list
                    Log.i("uuuuuussssss", list.toString())
                }
            }
        }
    }

    fun phoneNo(phoneNo: String): String {
        phoneNo.trim()
        var restult: String = phoneNo
        if(phoneNo[0]=='+'){
            restult = phoneNo.drop(3)
        }
        return restult.replace("\\s".toRegex(),"")
    }


//---------------------------------------------------------------------------------------------------------------------
    suspend fun getUserByPhone(phone: String):Task<DataSnapshot>{
        val job = GlobalScope.async {
            database.getReference("Users").child(phone).child("UserData").get()
        }
        return job.await()
    }

    private val _statusBar = MutableLiveData<Boolean>()
    val statusBar : LiveData<Boolean> = _statusBar
    suspend fun upLoadStatus(file: Uri?, phone: String,text: String?){
        _statusBar.value = false
        GlobalScope.launch(Dispatchers.IO) {
            if(file != null){
                val ref = storage?.child(phone)?.child("Status")
                    ?.child(keyGen())
                ref?.putFile(file!!)?.onSuccessTask {
                    it.storage.downloadUrl.addOnSuccessListener {
                        setStatus(it.toString(),phone,keyGen(),text)
                        _statusBar.value = true
                    }
                }
            }else{
            }
        }
    }

    fun keyGen():String{
        val ts = System.currentTimeMillis()/1000
        Log.i("TimeStamp key",ts.toString())
        return ts.toString()
    }

    private fun setStatus(uri: String,phone: String?,time:String,text:String?) {
        GlobalScope.launch(Dispatchers.IO) {

            for(contact in users.value as List){
                var ref =  database.getReference("Users").child(contact.phone.toString()!!).child("Statuses").child("others")
                ref.child(phone!!).setValue(StatusDetails(phone,time,uri))
            }
            var ref = database.getReference("Users").child(phone!!).child("Statuses").child("MyStatus")
            ref.child("status").child(time)
                .setValue(StatusModel(uri,keyGen().toLong(),text))
            ref.child("details")
                .setValue(StatusDetails(phone,time,uri))
        }
    }


    suspend fun getStatus(phone: String) = callbackFlow<Result<List<StatusModel>>> {
        val messageListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.children.map { ds->
                    ds.getValue(StatusModel::class.java)
                }
                Log.i("flowmss",status.toString())
                this@callbackFlow.trySendBlocking(Result.success(status.filterNotNull()))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
            }
        }
        database.getReference("Users").child(phone).child("Statuses").child("MyStatus").child("status")
            .addValueEventListener(messageListener)
        awaitClose {
            database.getReference("Users").child(phone).child("Statuses").child("MyStatus").child("status")
                .removeEventListener(messageListener)
        }
    }

    fun getStatusFlow(phone: String) = callbackFlow<Result<List<StatusDetails>>> {
        val messageListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val status = snapshot.children.map { ds->
                    ds.getValue(StatusDetails::class.java)
                }
                Log.i("flowmss",status.toString())
                this@callbackFlow.trySendBlocking(Result.success(status.filterNotNull()))
            }

            override fun onCancelled(error: DatabaseError) {
                this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
            }
        }
        database.getReference("Users").child(phone).child("Statuses").child("others")
            .addValueEventListener(messageListener)
        awaitClose {
            database.getReference("Users").child(phone).child("Statuses").child("others")
                .removeEventListener(messageListener)
        }
    }

    fun setSeen(phone:String,user:String){
        database.getReference("Users").child(phone).child("Statuses").child("others")
            .child(user).child("seen").setValue(true)
    }

    fun deleteStatus(){

    }

}
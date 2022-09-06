package com.example.whatsup.firebaserpository

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.ImageDecoder.ImageInfo
import android.os.Message
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.whatsup.model.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import kotlin.collections.ArrayList


@SuppressLint("Range")
class FirebaseDB(val application: Application){

   val database = Firebase.database
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
      }else {
         Log.i(number, "noPresent")
      }
   }
}
//-------------------------------------------CONTACTS-----------------------------------------------------------------------
   private val _usersPhone = MutableLiveData<List<ContactsModel>>()
   val usersPhone : LiveData<List<ContactsModel>> = _usersPhone

   fun getList(){
      val dataBaseReference = database.getReference("Users")
      var list = ArrayList<ContactsModel>()
      for(user in allContactList){
         dataBaseReference.child(user.phone.toString()).get().addOnSuccessListener {
            if(it.exists()){
               list.add(user)
               _usersPhone.value = list
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

//--------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------CURRENT--- USER---------------------------------------------------
   private val _currentUserData = MutableLiveData<User>()
   val currentUserData : LiveData<User> = _currentUserData


   fun getUserData(string: String){
      val dataBaseReference = database.getReference("Users").child(string)

      dataBaseReference.child("UserData").get().addOnSuccessListener {
         if(it.exists()){
           var user = User(it.child("userName").value.toString(),
           it.child("mail").value.toString(),
           it.child("password").value.toString(),
           it.child("phoneNo").value.toString())
            _currentUserData.value = user
            Log.i("_current",currentUserData.value.toString())
         }
      }.addOnFailureListener {

      }
   }
//--------------------------------------------------------------------------------------------------------------------------

   private val _usersChat = MutableLiveData<List<String>>()
   val usersChats : LiveData<List<String>> = _usersChat
   fun sendMessage(messageModel: MessageModel?){
      val db = database.getReference("Users").child(messageModel?.senderPhone.toString())
      db.child("chats").child(messageModel?.receiverPhone.toString()).get().addOnSuccessListener {
         if(it.exists()){
            val db1 = database.getReference("Users").child(messageModel?.senderPhone.toString()).child("chats")
            db1.child(messageModel?.receiverPhone.toString()).child("lastMeg").setValue(messageModel?.msg)
            db1.child(messageModel?.receiverPhone.toString()).child("time").setValue(messageModel?.time)

            val db2 = database.getReference("Users").child(messageModel?.receiverPhone.toString()).child("chats")
            db2.child(messageModel?.senderPhone.toString()).child("lastMeg").setValue(messageModel?.msg)
            db2.child(messageModel?.senderPhone.toString()).child("time").setValue(messageModel?.time)
         }else{
            val db1 = database.getReference("Users").child(messageModel?.senderPhone.toString()).child("chats")
            db1.child(messageModel?.receiverPhone.toString()).
            setValue(ChatsModel(messageModel?.receiverPhone.toString(),
               messageModel?.msg.toString(),messageModel?.time.toString()))


            val db2 = database.getReference("Users").child(messageModel?.receiverPhone.toString()).child("chats")
            db2.child(messageModel?.senderPhone.toString()).
            setValue(ChatsModel(messageModel?.senderPhone.toString(),
               messageModel?.msg.toString(),messageModel?.time.toString()))
         }
      }
      createChats(messageModel)
   }

   fun createChats(messageModel: MessageModel?){

      //user---------------------------------------------
      val db1 = database.getReference("Users").child(messageModel?.senderPhone.toString())
      db1.child("Messages").child(messageModel?.receiverPhone.toString()).child(messageModel?.key.toString()).
      setValue(messageModel)

      //receiver----------------------------------------
      val db2 = database.getReference("Users").child(messageModel?.receiverPhone.toString())
      db2.child("Messages").child(messageModel?.senderPhone.toString()).child(messageModel?.key.toString()).
      setValue(messageModel)

   }
//------------------------------------------------------------------Read message----------------------------

   fun getMessageFlow(chatPhone: String,senderPhone: String) = callbackFlow<Result<List<MessageModel>>> {
      var i=0
      val messageListener = object : ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
            Log.i("DATASNAP",i++.toString())
            val message = snapshot.children.map { ds->
               ds.getValue(MessageModel::class.java)
            }
            Log.i("flowmss",message.toString())
            this@callbackFlow.trySendBlocking(Result.success(message.filterNotNull()))
         }

         override fun onCancelled(error: DatabaseError) {
            this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
         }
      }

      database.getReference("Users").child(senderPhone).child("Messages").child(chatPhone).
              addValueEventListener(messageListener)
      awaitClose {
         database.getReference("Users").child(senderPhone).child("Messages").child(chatPhone).
         removeEventListener(messageListener)
      }
   }
   //---------------------------------------------------------CHATS------------------------------------------------------

   fun getChatsFlow(user: String) = callbackFlow<Result<List<UserChatsModel>>>{
      val chatsListener = object  : ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
            val chats = snapshot.children.map {ds ->
               ds.getValue(UserChatsModel::class.java)
            }
            this@callbackFlow.trySendBlocking(Result.success(chats.filterNotNull()))
         }

         override fun onCancelled(error: DatabaseError) {
            this@callbackFlow.trySendBlocking(Result.failure(error.toException()))
         }
      }
      database.getReference("Users").child(user).child("chats").addValueEventListener(chatsListener)
      awaitClose{
         database.getReference("Users").child(user).child("chats").removeEventListener(chatsListener)
      }
   }

   val _chatsModel = MutableLiveData<List<UserData>>()
   val chatsModel : LiveData<List<UserData>> = _chatsModel

   fun getChats(user: String){
      val db = database.getReference("Users").child(user).child("chats")
      db.addValueEventListener(object :ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
            val list = ArrayList<UserData>()
            for(dataSnapshot in snapshot.children){
               list.add(UserData(findContact(dataSnapshot.child("phoneNo").value.toString()),
                  dataSnapshot.child("phoneNo").value.toString(),
                  " ",
                  dataSnapshot.child("lastMeg").value.toString(),
               dataSnapshot.child("time").value.toString()))
               _chatsModel.value = list
            }
            Log.i("chats1",list.toString())
         }

         override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
         }
      })
   }

   private fun findContact(phoneNo : String): String{
      for (num in allContactList ){
         if(num.phone == phoneNo){
            return num.name.toString()
         }
      }
      return phoneNo
   }

//--------------------------------------------------------------------------------------------------------
   fun getMessage(user:String,receiver:String):Int{
      var i=0;
      val db = database.getReference("Users").child(user).child("Messages").child(receiver)
      db.addValueEventListener(object : ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
               for (ds in snapshot.children){
                  if(ds.child("seen").value==false){
                     i++;
                  }
               }
         }

         override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
         }
      })
      return i
   }
//---------------------------------------------------------------------------------------------
   fun changeSeen(sender:String,receiver:String,key:String){
      database.getReference("Users").child(sender).child("Messages").child(receiver).child(key).
              child("seen").setValue(true)
      database.getReference("Users").child(receiver).child("Messages").child(sender).child(key).
            child("seen").setValue(true)
      Log.i("Keyyyy2",key.toString())
   }

}
package com.example.whatsup.viewmodel

import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.whatsup.firebaserpository.FirebaseDB
import com.example.whatsup.model.ContactsModel
import com.example.whatsup.model.MessageModel
import com.example.whatsup.model.User
import com.example.whatsup.model.UserData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FirebaseDBViewModel(private val repository: FirebaseDB): ViewModel(){


    val usersPhone : LiveData<List<ContactsModel>> = repository.usersPhone
    val currentUserData : LiveData<User> = repository.currentUserData
    val m = MutableLiveData<List<MessageModel>>()
    val chats: LiveData<List<UserData>> = repository.chatsModel

    fun getAllContactsList(){
            repository.getList()
            Log.i("LiveList",repository.usersPhone.value.toString())
    }

    fun sendMessage(messageModel: MessageModel){
        repository.sendMessage(messageModel)
    }
    fun getMessageFlow(chatPhone: String, senderPhone:String){
        viewModelScope.launch {
            repository.getMessageFlow(chatPhone, senderPhone).collect{
                when{
                    it.isSuccess ->{
                        m.value = it.getOrNull()
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    fun getChats(string: String){
        viewModelScope.launch {
            repository.getChats(string)
        }
    }


    fun setMessageSeen(sender:String,receiver:String){
        for(message in m.value as List<MessageModel>){
            if((message.seen == false) &&
                (message.senderPhone == sender)){
                repository.changeSeen(sender,receiver,message.key.toString())
            }
        }
    }

    fun uploadPhoto(uri: Uri){
        repository.uploadImage(uri)
    }

init {
    getUserData()
}
    fun getUserData(){
        var sharedPreferences = repository.application.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var phone = sharedPreferences.getString("phone","1")
        repository.getUserData(phone.toString())
    }
}

class FirebaseDBViewModelFactory(val repository: FirebaseDB): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FirebaseDBViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FirebaseDBViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
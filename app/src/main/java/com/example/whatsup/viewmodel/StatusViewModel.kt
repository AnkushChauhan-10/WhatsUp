package com.example.whatsup.viewmodel

import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.whatsup.firebaserpository.StatusDao
import com.example.whatsup.model.ContactsModel
import com.example.whatsup.model.StatusDetails
import com.example.whatsup.model.StatusModel
import com.example.whatsup.model.User
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class StatusViewModel(private val repository: StatusDao):ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    val currentUser : LiveData<User> = _currentUser
    val userPhone = MutableLiveData<List<StatusDetails>>()
    private var _status = MutableLiveData<List<StatusModel>>()
    val status : LiveData<List<StatusModel>> = _status
    val statusBar: LiveData<Boolean> = repository.statusBar
    val contact: LiveData<List<ContactsModel>> = repository.users

   fun getList(){
       // repository.getList()
    }

    fun getUserByPhone(){
        var sharedPreferences = repository.application.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var phone = sharedPreferences.getString("phone","1")
        viewModelScope.async{
            repository.getUserByPhone(phone.toString()).addOnCompleteListener {
                    _currentUser.value = it.result.getValue(User::class.java)
                }
            }
    }

    fun upLoadStatus(uri: Uri){
        viewModelScope.async {
            repository.upLoadStatus(uri,_currentUser.value?.phoneNo.toString())
        }
    }

    fun getStatus(phone:String){
        viewModelScope.async {
            repository.getStatus(phone).addOnCompleteListener{
                var list = ArrayList<StatusModel>()
                Log.i("StatusCheck","---------------------------------------------------------------------")
                Log.i("StatusCheck0",it.result.childrenCount.toString())
                for(i in it.result.children){
                    list.add(i.getValue(StatusModel::class.java)!!)
                }
                Log.i("StatusCheck1",list.size.toString())
                _status.value = list
                Log.i("StatusCheck2", (status.value as ArrayList<StatusModel>).size.toString())
            }
        }
    }

    fun getStatusFlow(phone: String){
        viewModelScope.launch {
            repository.getStatusFlow(phone).collect{
                when{
                    it.isSuccess ->{
                        userPhone.value = it.getOrNull()
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
            }
        }
    }

    fun setStatusSeen(phone: String,user:String){
        repository.setSeen(phone,user)
    }

    fun det(){
        _status.value = ArrayList<StatusModel>()
    }
}

class StatusViewModelFactory(val repository: StatusDao): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return StatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
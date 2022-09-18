package com.example.whatsup.viewmodel

import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.example.whatsup.firebaserpository.StatusDao
import com.example.whatsup.model.*
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

    private val _user = MutableLiveData<User>()
    val user : LiveData<User> = _user

    fun getUserByPhone(){
        var sharedPreferences = repository.application.getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var phone = sharedPreferences?.getString("phone","1")
        viewModelScope.async{
            repository.getUserByPhone(phone.toString()).addOnCompleteListener {
                    _currentUser.value = it.result.getValue(User::class.java)
                }
            }
    }

    fun getUser(phone: String){
        viewModelScope.async {
            repository.getUserByPhone(phone).addOnCompleteListener {
                _user.value = it.result.getValue(User::class.java) as User
            }
        }
    }

    fun upLoadStatus(uri: String?,text:String){
        Log.i("UsersStatus","Upload------"+Uri.parse(uri).toString())
        viewModelScope.async {
            repository.upLoadStatus(Uri.parse(uri),_currentUser.value?.phoneNo.toString(),text)
        }
    }

    fun getStatus(phone:String){
        viewModelScope.launch {
            repository.getStatus(phone).collect{
                when{
                    it.isSuccess ->{
                        _status.value = it.getOrNull()
                    }
                    it.isFailure -> {
                        it.exceptionOrNull()?.printStackTrace()
                    }
                }
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
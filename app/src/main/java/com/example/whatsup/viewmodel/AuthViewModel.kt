package com.example.whatsup.viewmodel

import android.os.Build
import android.text.BoringLayout
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whatsup.firebaserpository.AuthenticationRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.IllegalArgumentException

class AuthViewModel(private val repo: AuthenticationRepository): ViewModel() {

    val data : LiveData<FirebaseUser> = repo.data
    val loggedIn : LiveData<Boolean> = repo.loggedIn

    fun singIn(email: String, password: String){
        repo.logIn(email,password)
    }

    fun singUp(userName: String, email: String,password: String,phoneNo: String){
        val phone = phoneNo(phoneNo)
        repo.singUp(userName,email,password,phone)
    }

    fun phoneNo(phoneNo: String): String {
        phoneNo.trim()
        var restult: String = phoneNo
        if(phoneNo[0]=='+'){
            restult = phoneNo.drop(3)
        }
        return restult.replace("\\s".toRegex(),"")
    }

}
class AuthViewModelFactory(private val repo: AuthenticationRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
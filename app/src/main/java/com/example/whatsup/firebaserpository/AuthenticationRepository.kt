package com.example.whatsup.firebaserpository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.whatsup.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AuthenticationRepository(private val context: Context) {

    private lateinit var auth: FirebaseAuth
    private lateinit var dataBaseReference: DatabaseReference
    private val _data = MutableLiveData<FirebaseUser>()
    val data : LiveData<FirebaseUser> = _data
    val loggedIn =  MutableLiveData<Boolean>()


    fun logIn(email: String, password: String){
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    loggedIn.value = true
                    _data.value = auth.currentUser
                }else{
                    loggedIn.value = false
                }
            }
    }

    fun singUp(userName: String, email: String,password: String,phoneNo: String){
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                loggedIn.value = true
                _data.value = auth.currentUser
                var user = User(userName,email,password, phoneNo)
                dataBaseReference = FirebaseDatabase.getInstance("https://whats-up-1e69b-default-rtdb.firebaseio.com/")
                    .getReference("Users")
                GlobalScope.launch(Dispatchers.IO) {
                    dataBaseReference.child(phoneNo).child("UserData").setValue(user)
                }
            }else{
                loggedIn.value = false
            }
        }
    }

}